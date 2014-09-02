package br.com.pwms;

import java.io.IOException;

public class Principal {
	public static void main(String[] args) {
		try {
			new PDFtoHTML().lerXML();
		} catch (IOException e) {
			System.out.println("Erro ao converter o PDF");
		}
	}
}
