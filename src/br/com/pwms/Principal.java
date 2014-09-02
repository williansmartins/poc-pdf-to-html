package br.com.pwms;

import java.io.IOException;

public class Principal {
	
  private static final String NOME = "exemplo4";
  private static final String OUTPUT_ZIP_FILE = "C:\\dev\\pocs\\pdf\\MyFile.zip";
  private static final String SOURCE_FOLDER = "C:\\dev\\pocs\\html-gerado";
  private static final String PDF = "C:\\dev\\pocs\\pdf\\" + NOME + ".pdf";
	
	public static void main(String[] args) {
		try {
			new PDFtoIMG().start(PDF);
			new AppZip().main(SOURCE_FOLDER, OUTPUT_ZIP_FILE);
		} catch (IOException e) {
			System.out.println("Erro ao converter o PDF: " + e.getMessage());
		}
	}
}
