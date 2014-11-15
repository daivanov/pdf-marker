package org.indt.divanov;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextMarginFinder;

public class PdfProcessor {

	private static final int PAGE = 1;
	private static final int MARKER_SIZE = 1;

	public static void mark(String pdfInFile, String pdfOutFile)
			throws IOException, DocumentException {

		System.out.println("Loading " + pdfInFile);
		PdfReader reader = new PdfReader(pdfInFile);
		PdfReaderContentParser parser = new PdfReaderContentParser(reader);
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(pdfOutFile));
		TextMarginFinder finder = parser.processContent(PAGE, new TextMarginFinder());
		PdfContentByte cb = stamper.getOverContent(PAGE);
		drawMarker(cb, "1", MARKER_SIZE,
				finder.getLlx() - MARKER_SIZE / 2,
				finder.getUry() + MARKER_SIZE / 2);
		System.out.println("Saving to  " + pdfOutFile);
		stamper.close();
		reader.close();
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