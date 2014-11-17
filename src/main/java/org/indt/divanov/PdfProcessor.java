package org.indt.divanov;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.Matrix;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

public class PdfProcessor {

	private static final int PAGE = 1;
	private static final int MARKER_SIZE = 10;

	private static class MarginFinder implements RenderListener {
		private Rectangle2D.Float boundingBox = null;

		public Rectangle2D.Float getBoundingBox() {
			return boundingBox;
		}

		@Override
		public void beginTextBlock() {
		}

		@Override
		public void endTextBlock() {
		}

		@Override
		public void renderImage(ImageRenderInfo renderInfo) {
			Matrix ctm = renderInfo.getImageCTM();
			Rectangle2D.Float imageBox = new Rectangle2D.Float(
				ctm.get(Matrix.I31), ctm.get(Matrix.I32), 0, 0);
			try {
				// TODO: take into account possible rotation of image
				BufferedImage image = renderInfo.getImage().getBufferedImage();
				imageBox.width = (float)image.getWidth();
				imageBox.height = (float)image.getHeight();
			} catch(IOException e) {
				System.err.println("Supressed: " + e.getMessage());
			}
			if (boundingBox == null) {
				boundingBox = imageBox;
			} else {
				boundingBox.add(imageBox);
			}
		}

		@Override
		public void renderText(TextRenderInfo renderInfo) {
			if (boundingBox == null) {
				boundingBox = renderInfo.getDescentLine().getBoundingRectange();
			} else {
				boundingBox.add(renderInfo.getDescentLine().getBoundingRectange());
			}
			boundingBox.add(renderInfo.getAscentLine().getBoundingRectange());
		}
	}

	public static void mark(String pdfInFile, String pdfOutFile)
			throws IOException, DocumentException {

		System.out.println("Loading " + pdfInFile);
		PdfReader reader = new PdfReader(pdfInFile);
		PdfReaderContentParser parser = new PdfReaderContentParser(reader);
		Rectangle2D.Float bbox = getBoundingBox(parser, PAGE);
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(pdfOutFile));
		PdfContentByte cb = stamper.getOverContent(PAGE);
		float x = bbox.x - MARKER_SIZE / 2;
		if (x < MARKER_SIZE / 2) {
			x = MARKER_SIZE / 2;
		}
		float y = bbox.y + bbox.height + MARKER_SIZE / 2;
		if (y > reader.getPageSizeWithRotation(PAGE).getHeight() - MARKER_SIZE / 2) {
			y = reader.getPageSizeWithRotation(PAGE).getHeight() - MARKER_SIZE / 2;
		}
		drawMarker(cb, "1", MARKER_SIZE, x, y);
		System.out.println("Saving to  " + pdfOutFile);
		stamper.close();
		reader.close();
	}

	private static Rectangle2D.Float getBoundingBox(
			PdfReaderContentParser parser, int page) throws IOException {
		MarginFinder finder = parser.processContent(page, new MarginFinder());
		return finder.getBoundingBox();
	}

	
	private static final float SINUS = (float)Math.sin(Math.PI / 4);
	private static final float COSINUS = (float)Math.sqrt(1 - SINUS * SINUS);

	private static void drawMarker(PdfContentByte cb, String text, float size, float x, float y)
			throws IOException, DocumentException {

		cb.saveState();
		BaseFont bf = BaseFont.createFont();
		cb.beginText();
		cb.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL);
		cb.setLineWidth(0.5f);
		cb.setRGBColorStroke(0x22, 0x82, 0x22);
		cb.setRGBColorFill(0x22, 0x82, 0x22);
		cb.setFontAndSize(bf, size);
		cb.setTextMatrix(COSINUS, SINUS, -SINUS, COSINUS, x, y - size / 2.2f);
		cb.showText(text);
		cb.endText();
		cb.circle(x, y, size / 2);
		cb.stroke();
		cb.restoreState();
	}
}