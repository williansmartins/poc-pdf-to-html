package br.com.pwms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class PDFtoHTML {

	public void lerXML() throws FileNotFoundException, UnsupportedEncodingException, IOException {
		if( new PDFtoIMG().setup() ){
			XStream xstream = new XStream(new StaxDriver());
			xstream.alias("manifest", Manifest.class);
			xstream.alias("item", Item.class);
			
			File xmlFile = new File("xml.xml");
			Manifest manifest = (Manifest)xstream.fromXML(xmlFile);
			
			generate(manifest);
		}
		
	}

	public void generate(Manifest manifest)
			throws FileNotFoundException, UnsupportedEncodingException,
			IOException {
		PrintWriter writer = new PrintWriter("C:\\dev\\pocs\\html-gerado\\index.html",
				"UTF-8");

		File cabecalho = new File("C:\\dev\\pocs\\base-html\\cabecalho.html");
		FileInputStream fis = null;
		fis = new FileInputStream(cabecalho);

		int content;
		while ((content = fis.read()) != -1) {
			writer.print((char) content);
		}

		writer.println("");
		for (int i = 0; i < manifest.getItems().size(); i++) {
			writer.println("\t\t\t\t\t\t<li><a id='menu" + (i+1)
					+ "'  data-previous-menu='menu" + (i)
					+ "' data-next-menu='menu" + (i + 2)
					+ "' data-page='"+ manifest.getItems().get(i).getUrl() 
					+ "' onclick='new APIContentPDF().goToPage(" + (i+1)
					+ ", this)'  target='frameTarget'>"
					+ manifest.getItems().get(i).getTitle() + "</a></li>");
		}
		writer.println("\t\t\t\t\t\t<input type='hidden' id='totalPage' value='"
				+ (manifest.getItems().size()) + "'/>");

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
