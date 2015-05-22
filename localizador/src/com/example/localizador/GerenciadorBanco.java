package com.example.localizador;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class GerenciadorBanco {

	private Context context;

	public GerenciadorBanco(Context context) {
		this.context = context;
		atualiza();
	}

	private void atualiza() {
		// Verifica o servidor procurando por atualizacoes
	}

	public void inicializaBancos(BancoDados banco) {

		InputStream input;
		XmlPullParserFactory factory;
		XmlPullParser parser;

		try {
			input = context.getAssets().open("banco.xml");
			factory = XmlPullParserFactory.newInstance();
			parser = factory.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(input, null);
			parserXML(parser, banco);

		} catch (XmlPullParserException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void parserXML(XmlPullParser parser, BancoDados banco) throws XmlPullParserException, IOException {
		ArrayList<Local> locais = null;
		int eventType = parser.getEventType();
		String areaAtual = null;
		String responsavelAtual = "";
		Local localAtual = null;
		long id_local = 1L;

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String nomeTag = null;

			switch (eventType) {

			case XmlPullParser.START_DOCUMENT:
				locais = new ArrayList<Local>();
				break;

			case XmlPullParser.START_TAG:
				nomeTag = parser.getName();

				if (nomeTag.equalsIgnoreCase("nome") && localAtual == null) {
					areaAtual = parser.nextText();
				}

				// if (nomeTag.equalsIgnoreCase("info")) {
				// eventType = parser.next();
				// areaAtual = parser.nextText();
				// Toast.makeText(context, areaAtual, Toast.LENGTH_LONG).show();
				// }

				if (nomeTag.equalsIgnoreCase("responsavel")) {
					responsavelAtual = parser.nextText();
				}

				if (nomeTag.equalsIgnoreCase("local")) {
					localAtual = new Local();
				}
				if (nomeTag.equalsIgnoreCase("nome") && localAtual != null) {	
					localAtual.setNome(parser.nextText());
				}
				if (nomeTag.equalsIgnoreCase("latitude")) {
					localAtual.setLatitude(parser.nextText());
				}
				if (nomeTag.equalsIgnoreCase("longitude")) {
					localAtual.setLongitude(parser.nextText());
				}
				if (nomeTag.equalsIgnoreCase("altura")) {
					localAtual.setAltitude(parser.nextText());
				}
				if (nomeTag.equalsIgnoreCase("descricao")) {
					localAtual.setDescricao(parser.nextText());
				}

				break;

			case XmlPullParser.END_TAG:
				nomeTag = parser.getName();

				if (nomeTag.equalsIgnoreCase("local")) {
					locais.add(localAtual);
					localAtual = null;
				}

				if (nomeTag.equalsIgnoreCase("area")) {
					long idAreaAtual = banco.addArea(areaAtual, responsavelAtual);
					Iterator<Local> it = locais.iterator();
					while (it.hasNext()) {
						banco.addLocal(it.next(),id_local, idAreaAtual);
						id_local = id_local + 1L;
					}
					locais.clear();
					localAtual = null;
				}

				break;

			}

			eventType = parser.next();
		}
	}
	
	public void inicializaNos(BancoDados banco){
		InputStream input;
		XmlPullParserFactory factory;
		XmlPullParser parser;

		try {
			input = context.getAssets().open("nos.xml");
			factory = XmlPullParserFactory.newInstance();
			parser = factory.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(input, null);
			
			Log.e("NO","Arquivo de nos econtrado");
			
			parserXMLnos(parser, banco);

		} catch (XmlPullParserException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private void parserXMLnos(XmlPullParser parser, BancoDados banco) throws XmlPullParserException, IOException{
		int eventType = parser.getEventType();
		String id_no = "";
		String latAtual = "";
		String lngAtual = "";
		String ligAtual = "";

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String nomeTag = null;

			switch (eventType) {

			case XmlPullParser.START_DOCUMENT:
				break;

			case XmlPullParser.START_TAG:
				nomeTag = parser.getName();
				
				

				if (nomeTag.equalsIgnoreCase("id")) {
					id_no = parser.nextText();
					//Log.i("GerenciadorBanco => parserXMLnos", "No adicionado ao grafo: " + id_no);
				}

				if (nomeTag.equalsIgnoreCase("latitude")) {
					latAtual = parser.nextText();
				}
				if (nomeTag.equalsIgnoreCase("longitude")) {
					lngAtual = parser.nextText();
				}
				if (nomeTag.equalsIgnoreCase("liga")) {
					ligAtual = parser.nextText();
				}
				break;

			case XmlPullParser.END_TAG:
				nomeTag = parser.getName();

				Log.e("NO",nomeTag);
				
				if (nomeTag.equalsIgnoreCase("no")) {
					Log.e("NO", "Fechando no de latitude = " + String.valueOf(latAtual));
					long temp = banco.addNo(id_no,Double.valueOf(latAtual),Double.valueOf(lngAtual),ligAtual);
					Log.e("NO",String.valueOf(temp));
				}
				break;
			}
			eventType = parser.next();
		}
	}

}
