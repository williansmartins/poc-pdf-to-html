package br.com.pwms;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class PDFtoIMG {
	
    public boolean start(String pdf) throws IOException {
        // load a pdf from a byte buffer
        File file = new File( pdf );
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        FileChannel channel = raf.getChannel();
        ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        PDFFile pdffile = new PDFFile(buf);
        int numPgs = pdffile.getNumPages();
        for (int i = 0; i <= numPgs; i++) {
            // draw the first page to an image
            PDFPage page = pdffile.getPage(i);
            // get the width and height for the doc at the default zoom
            Rectangle rect = new Rectangle(0, 0, (int) page.getBBox().getWidth(), (int) page.getBBox().getHeight());
            // generate the image
            Image img = page.getImage(rect.width+200, rect.height+200, // width & height
                    rect, // clip rect
                    null, // null for the ImageObserver
                    true, // fill background with white
                    true // block until drawing is done
                    );
            // save it as a file
            BufferedImage bImg = toBufferedImage(img);
            System.out.println((i+1) + " >>>" + img );
            File yourImageFile = new File("C:\\dev\\pocs\\base-html\\img\\" + "page_" + (i) + ".png");
            ImageIO.write(bImg, "png", yourImageFile);
        }
        generateHTML(numPgs);
        return true;
    }

    public BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent
        // Pixels
        boolean hasAlpha = hasAlpha(image);
        // Create a buffered image with a format that's compatible with the
        // screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }
            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }

    public boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;
            return bimage.getColorModel().hasAlpha();
        }
        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }
        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }

	public void generateHTML(int qtd) throws FileNotFoundException, UnsupportedEncodingException, IOException {
		PrintWriter writer = new PrintWriter("C:\\dev\\pocs\\html-gerado\\index.html", "UTF-8");

		File cabecalho = new File("C:\\dev\\pocs\\base-html\\cabecalho.html");
		FileInputStream fis = null;
		fis = new FileInputStream(cabecalho);

		int content;
		while ((content = fis.read()) != -1) {
			writer.print((char) content);
		}

		writer.println("");
		for (int i = 0; i < qtd; i++) {
			writer.println("\t\t\t\t\t\t<li><a id='menu" + (i+1)
					+ "'  data-previous-menu='menu" + (i)
					+ "' data-next-menu='menu" + (i + 2)
					+ "' data-page='page_" + (i+1) + ".png"
					+ "' onclick='new APIContentPDF().goToPage(" + (i+1)
					+ ", this)'  target='frameTarget'>"
					+ "Página " + (i+1) + "</a></li>");
		}
		writer.println("\t\t\t\t\t\t<input type='hidden' id='totalPage' value='"
				+ (qtd) + "'/>");

		File rodape = new File("C:\\dev\\pocs\\base-html\\rodape.html");
		fis = new FileInputStream(rodape);

		content = 0;
		while ((content = fis.read()) != -1) {
			writer.print((char) content);
		}

		writer.close();
		fis.close();

		File cssSrcFolder = new File("C:\\dev\\pocs\\base-html\\css");
		File jsSrcFolder = new File("C:\\dev\\pocs\\base-html\\js");
		File imgSrcFolder = new File("C:\\dev\\pocs\\base-html\\img");
		
		File cssDestFolder = new File("C:\\dev\\pocs\\html-gerado\\css");
		File jsDestFolder = new File("C:\\dev\\pocs\\html-gerado\\js");
		File imgDestFolder = new File("C:\\dev\\pocs\\html-gerado\\img");


		// make sure source exists
		if (!cssSrcFolder.exists()) {

			System.out.println("Directory does not exist.");
			// just exit
			System.exit(0);

		} else {

			try {
				copyFolder(cssSrcFolder, cssDestFolder);
			} catch (IOException e) {
				e.printStackTrace();
				// error, just exit
				System.exit(0);
			}
		}

		// make sure source exists
		if (!jsSrcFolder.exists()) {

			System.out.println("Directory does not exist.");
			// just exit
			System.exit(0);

		} else {

			try {
				copyFolder(jsSrcFolder, jsDestFolder);
			} catch (IOException e) {
				e.printStackTrace();
				// error, just exit
				System.exit(0);
			}
		}
		
		// make sure source exists
		if (!imgSrcFolder.exists()) {
			System.out.println("Directory does not exist.");
		} else {
			try {
				copyFolder(imgSrcFolder, imgDestFolder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Done");
	}

	public void copyFolder(File src, File dest) throws IOException {

		if (src.isDirectory()) {

			// if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdir();
				System.out.println("Directory copied from " + src + "  to "
						+ dest);
			}

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile);
			}

		} else {
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
			System.out.println("File copied from " + src + " to " + dest);
		}
	}
}