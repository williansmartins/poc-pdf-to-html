package br.com.pwms;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class GerarSomenteIMG {

	public static void main(String[] args) throws IOException {
		new GerarSomenteIMG().start();
	}

	public boolean start() throws IOException {
		// load a pdf from a byte buffer
		File file = new File("C:\\dev\\pocs\\pdf\\conteudo-1.7-5-paginas.pdf");
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		FileChannel channel = raf.getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0,
				channel.size());
		PDFFile pdffile = new PDFFile(buf);
		int numPgs = pdffile.getNumPages();
		for (int i = 0; i <= numPgs; i++) {
			// draw the first page to an image
			PDFPage page = pdffile.getPage(i);
			// get the width and height for the doc at the default zoom
			Rectangle rect = new Rectangle(0, 0, (int) page.getBBox()
					.getWidth(), (int) page.getBBox().getHeight());
			// generate the image
			Image img = page.getImage(rect.width + 200, rect.height + 200, // width
																			// &
																			// height
					rect, // clip rect
					null, // null for the ImageObserver
					true, // fill background with white
					true // block until drawing is done
					);
			// save it as a file
			BufferedImage bImg = new PDFtoIMG().toBufferedImage(img);
			System.out.println((i + 1) + " >>>" + img);
			File yourImageFile = new File("C:\\dev\\pocs\\base-html\\img\\"
					+ "page_" + (i) + ".png");
			ImageIO.write(bImg, "png", yourImageFile);
		}
		return true;
	}
}
