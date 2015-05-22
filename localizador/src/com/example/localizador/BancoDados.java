package com.example.localizador;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Classe responsável pelo banco de dados que guarda os locais e areas
 * existentes na aplicação e suas respectivas informações
 * 
 * @author Igor Couto
 * @version 1.0
 */
public class BancoDados extends SQLiteOpenHelper {


	/** Tag "BancoAreas" do Logcat, usada para depuração */
	protected static String LOG;

	/** Versão do banco de dados */
	protected static int DATABASE_VERSION = 1;

	// Colunas da Tabela Areas
	private static final String KEY_ID_AREA = "_id";
	private static final String KEY_NOME_AREA = "nome_area";
	private static final String KEY_DESCRICAO_AREA = "descricao_area";
	private static final String KEY_RESPONSAVEL = "responsavel";

	// Colunas da Tabela Locais
	private static final String KEY_ID_LOCAL = "_id";
	private static final String KEY_NOME_LOCAL = "nome_local";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";
	private static final String KEY_ALTITUDE = "altitude";
	private static final String KEY_DESCRICAO_LOCAL = "descricao";
	private static final String KEY_ID_AREA_LOCAL = "id_area";

	// Colunas da Tabela de Nos
	protected static final String KEY_ID_NO = "_id";
	protected static final String KEY_LAT_NO = "latitude";
	protected static final String KEY_LNG_NO = "longitude";
	protected static final String KEY_LIG_NO = "liga";

	protected static final String NOME_TABELA_AREAS = "areas";

	protected static final String NOME_TABELA_LOCAIS = "locais";

	protected static final String NOME_TABELA_NOS = "nos";

	protected static String CRIA_TABELA_LOCAIS;

	protected static String CRIA_TABELA_AREAS;

	protected static String CRIA_TABELA_LOC_POINTS;

	public BancoDados(Context context) {
		super(context, "BancoLocalizador", null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		CRIA_TABELA_LOCAIS = "CREATE TABLE " + NOME_TABELA_LOCAIS + "( " + KEY_ID_LOCAL + " INTEGER PRIMARY KEY," + KEY_NOME_LOCAL + " TEXT," + KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT," + KEY_ALTITUDE + " TEXT," + KEY_DESCRICAO_LOCAL + " TEXT," + KEY_ID_AREA_LOCAL + " INTEGER" + ")";
		CRIA_TABELA_AREAS = "CREATE TABLE " + NOME_TABELA_AREAS + "( " + KEY_ID_AREA + " INTEGER PRIMARY KEY," + KEY_NOME_AREA + " TEXT," + KEY_DESCRICAO_AREA + " TEXT," + KEY_RESPONSAVEL + " TEXT" + ")";
		CRIA_TABELA_LOC_POINTS = "CREATE TABLE " + NOME_TABELA_NOS + "( " + KEY_ID_NO + " INTEGER PRIMARY KEY," + KEY_LAT_NO + " REAL," + KEY_LNG_NO + " REAL," + KEY_LIG_NO + " TEXT" + ")";

		db.execSQL(CRIA_TABELA_LOC_POINTS);
		db.execSQL(CRIA_TABELA_AREAS);
		db.execSQL(CRIA_TABELA_LOCAIS);

		criaGrafo();

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_AREAS);
		db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_LOCAIS);
		db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_NOS);
		onCreate(db);
	}

	/**
	 * Fecha o Banco de dados
	 * 
	 */
	public void fechaBD() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	private void criaGrafo() {
		// SQLiteDatabase db = this.getReadableDatabase();
		// String selectQueryTotais = "SELECT * FROM " + NOME_TABELA_NOS;
		// Cursor cursorTotais = db.rawQuery(selectQueryTotais, null);
		//
		// int totais = cursorTotais.getCount();
		// for (int i = 0; i < totais; i++) {
		// Map<String, Double> mapA = new HashMap<String, Double>();
		// mapA.put("A", 0.0);
		// mapA.put("B", 10.0);
		// mapA.put("C", 20.0);
		// mapA.put("E", 100.0);
		// mapA.put("F", 110.0);
		// }

	}

	public Cursor getCursor(String lugar) {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + NOME_TABELA_LOCAIS + " WHERE " + KEY_NOME_LOCAL + " LIKE " + "'" + lugar + "%'";

		Log.i("BancoDados => getCursor", selectQuery);

		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			return cursor;
		} else
			return null;
	}

	public List<String> busca(String lugar, Local localAtual) {
		ArrayList<String> resultados = new ArrayList<String>();
		// List<Local> comparaveis = new ArrayList<Local>();
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + NOME_TABELA_LOCAIS + " WHERE " + KEY_NOME_LOCAL + " LIKE " + "'" + lugar + "%'";

		Log.i("BancoDados => busca", selectQuery);

		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {

			while (cursor.isAfterLast() == false) {
				String nome = cursor.getString(cursor.getColumnIndex(KEY_NOME_LOCAL));

				Local local = new Local();
				local.setNome(nome);
				local.setLatitude(cursor.getString(cursor.getColumnIndex(KEY_LATITUDE)));
				local.setLongitude(cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE)));

				nome = nome + " - " + getAreaNome(cursor.getLong(cursor.getColumnIndex(KEY_ID_AREA_LOCAL)));

				resultados.add(nome);

				// comparaveis.add(local);
				cursor.moveToNext();
			}
		}

		// if(localAtual != null){
		// Collections.sort(comparaveis, new LocalComparator(localAtual));
		// }

		// resultados.clear();

		// for(int i =0; i<comparaveis.size();i++){
		//
		// resultados.add(comparaveis.get(i).getNome());
		// }

		// String tmp = resultados.get(0);
		// resultados.set(0, resultados.get(1));
		// resultados.set(1, tmp);

		return resultados;
	}

	public class LocalComparator implements Comparator<Local> {

		public Local origem;

		public LocalComparator(Local origem) {
			this.origem = origem;
		}

		public int compare(Local local1, Local local2) {

			if (local1.compareTo(local2) == 0) {
				if (Local.distancia(origem, local1) > Local.distancia(origem, local1)) {
					return -1;
				} else
					return 1;
			} else
				return local1.compareTo(local2);
		}
	}

	public class No {
		public int dist;
		public boolean marcado;

		public No() {
			dist = Integer.MAX_VALUE;
			;
			marcado = false;
		}

	}

	public Local buscaExata(String lugar) {
		Local resultado = new Local();
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + NOME_TABELA_LOCAIS + " WHERE " + KEY_NOME_LOCAL + " LIKE " + "'" + lugar + "'";

		Log.i("BancoDados => buscaExata", selectQuery);

		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			resultado.setLatitude(cursor.getString(cursor.getColumnIndex(KEY_LATITUDE)));
			resultado.setLongitude(cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE)));
			resultado.setNome(cursor.getString(cursor.getColumnIndex(KEY_NOME_LOCAL)));
			return resultado;
		} else
			return null;

	}

	public PolylineOptions menorCaminhoANTIGO(LatLng coordAtual, LatLng coordDestino) {
		PolylineOptions caminho = new PolylineOptions();
		ArrayList<LatLng> pontosCaminho = new ArrayList<LatLng>();
		ArrayList<Long> caminhoNos = new ArrayList<Long>();
		ArrayList<No> nos = new ArrayList<No>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		
		
		caminho.add(coordAtual);
		

		// ENCONTRA NO INICIAL E FINAL

		long id_inicial = encontraNo(db, coordAtual);
		long id_final = encontraNo(db, coordDestino);
		
		Log.i("BuscaCaminho", "No inicial: " + id_inicial);
		Log.i("BuscaCaminho", "No final: " + id_final);
		

		// QUERY PARA SABER O TOTAL DE NOS NO BANCO

		String selectQueryTotais = "SELECT * FROM " + NOME_TABELA_NOS;
		Cursor cursorTotais = db.rawQuery(selectQueryTotais, null);

		int totais = cursorTotais.getCount();
		cursorTotais.close();
		
		Log.i("BuscaCaminho", "Total de nos: " + totais);
		
		for (int i = 0; i < totais; i++) {
			No n = new No();
			nos.add(n);
			caminhoNos.add(-1L);
		}

		// BuscaCaminho

		Log.i("BuscaCaminho", "Iniciando BuscaCaminho");

		Fila fila = new Fila();
		long id_atual; // No que ira ser analizado na iteracao. Serao calculadas
						// distancias a partir dele
		fila.enfileira(id_inicial); // Coloca o primeiro no da busca na fila

		nos.get((int)id_inicial -1).dist = 0; // Inicia a distancia do no inicial da
										// busca como 0

		while (!fila.vazia()) {
			id_atual = fila.desenfileira();
			Log.i("BuscaCaminho", "Desenfilerou : no " + id_atual);
			String selectQueryAtual = "SELECT * FROM " + NOME_TABELA_NOS + " WHERE " + KEY_ID_NO + " = " + String.valueOf(id_atual);
			Log.i("BuscaCaminho", "Query Principal:  " + selectQueryAtual);
			Cursor cursorAtual = db.rawQuery(selectQueryAtual, null);
			cursorAtual.moveToFirst();
			double lngAtual =  cursorAtual.getDouble(cursorAtual.getColumnIndex(KEY_LNG_NO));
			double latAtual =  cursorAtual.getDouble(cursorAtual.getColumnIndex(KEY_LAT_NO));
			
			String[] ligacoes = cursorAtual.getString(cursorAtual.getColumnIndex(KEY_LIG_NO)).split(" ");

			for (String ligacao_atual : ligacoes) {
				Log.i("BuscaCaminho", "Ligacao atual: " + ligacao_atual);
				if (nos.get(Integer.valueOf(ligacao_atual) -1 ).marcado == false)
					fila.enfileira(Integer.valueOf(ligacao_atual));

				String selectQuery = "SELECT * FROM " + NOME_TABELA_NOS + " WHERE " + KEY_ID_NO + " = " + ligacao_atual;
				Log.i("BuscaCaminho", "Query Iteração:  " + selectQuery);
				Cursor cursor = db.rawQuery(selectQuery, null);
				cursor.moveToFirst();
				long id_iteracao =  cursor.getLong(cursorAtual.getColumnIndex(KEY_ID_NO));
				int distancia = (int) Local.distancia(latAtual, lngAtual, cursor.getDouble(cursor.getColumnIndex(KEY_LAT_NO)), cursor.getDouble(cursor.getColumnIndex(KEY_LNG_NO)));
				cursor.close();
				if (distancia <= nos.get((int)id_iteracao -1).dist) {
					nos.get(Integer.valueOf(ligacao_atual) -1).dist = distancia;
					caminhoNos.add(Integer.valueOf(ligacao_atual) -1, id_atual);
				}
			}
			nos.get((int)id_atual -1).marcado = true;
		}

		Log.i("BuscaCaminho", "BuscaCaminho finalizada.");

		// Monta a polyline

		Log.i("BuscaCaminho", "Montando Polyline...");

		long id = id_inicial;
		
		Log.i("BuscaCaminho", "ID Inicial: "  + id);
		Log.i("BuscaCaminho", "ID Final: " + id_final);
		
		int count = 0 ;
		for(long i: caminhoNos){
			Log.i("BuscaCaminho","Indice: " + count + " Valor: " + caminhoNos.get(count));
			count++;
		}
		
		
		long id_anterior = -50;

		while (id != id_final) {
			id = caminhoNos.get((int)id -1)  ;
			
			
			if(id == -1){
				break;
			}
			
			if(id == id_anterior){
				break;
			}
			
			Log.i("BuscaCaminho", "ID ATUAL: " + id);

			String selectQuery = "SELECT * FROM " + NOME_TABELA_NOS + " WHERE " + KEY_ID_NO + " = " + String.valueOf(id - 1L);
			Log.i("BuscaCaminho", "ID ATUAL QUERY: " + selectQuery);
			Cursor cursor3 = db.rawQuery(selectQuery, null);
			cursor3.moveToFirst();
			LatLng ponto = new LatLng(cursor3.getDouble(cursor3.getColumnIndex(KEY_LAT_NO)), cursor3.getDouble(cursor3.getColumnIndex(KEY_LNG_NO)));
			cursor3.close();
			
			Log.i("BuscaCaminho", "Ponto da Polyline: Lat:" + ponto.latitude + "Lng: " + ponto.longitude);
			
			pontosCaminho.add(ponto);
			id_anterior = id;
			
			
		}
		
		
		String selectQuery = "SELECT * FROM " + NOME_TABELA_NOS + " WHERE " + KEY_ID_NO + " = " + String.valueOf(id + 1L);
		Cursor cursor2 = db.rawQuery(selectQuery, null);
		cursor2.moveToFirst();
		LatLng ponto = new LatLng(cursor2.getDouble(cursor2.getColumnIndex(KEY_LAT_NO)), cursor2.getDouble(cursor2.getColumnIndex(KEY_LNG_NO)));
		cursor2.close();
		Log.i("BuscaCaminho", "Ponto da Polyline: Lat:" + ponto.latitude + "Lng: " + ponto.longitude);
		
		pontosCaminho.add(ponto);
		
		
		

		Log.i("BuscaCaminho", "Pontos na polyline: " + pontosCaminho.size());
		
		Log.i("BuscaCaminho", "Polyline montada, retorna menor caminho.");

		caminho.addAll(pontosCaminho);
		
		
		caminho.add(coordDestino);
		
		
		caminho.width(6);
		caminho.color(Color.RED);
		
		

		return caminho;
	}

	public LatLng getNoByID(long id) {

		SQLiteDatabase db = this.getReadableDatabase();
		double lat;
		double lng;
		String idArea = "'" + id + "'";
		String selectQuery = "SELECT * FROM " + NOME_TABELA_NOS + " WHERE " + KEY_ID_NO + " = " + id;

		Log.i("BancoDados => getNoByID", selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			lat = c.getDouble(c.getColumnIndex(KEY_LAT_NO));
			lng = c.getDouble(c.getColumnIndex(KEY_LNG_NO));
		} else
			return null;

		return new LatLng(lat, lng);

	}

	public class Fila {
		ArrayList<Long> fila = new ArrayList<Long>();

		public void enfileira(long indice) {
			fila.add(indice);
		}

		public long desenfileira() {
			long valor = fila.get(0);
			fila.remove(0);
			return valor;
		}

		public boolean vazia() {
			return fila.isEmpty();
		}
	}

	public long addNo(String id, double lat, double lng, String liga) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		// values.put(KEY_ID_NO, Long.valueOf(id));
		values.put(KEY_LAT_NO, lat);
		values.put(KEY_LNG_NO, lng);
		values.put(KEY_LIG_NO, liga);

		return db.insert(NOME_TABELA_NOS, null, values);
	}

	/**
	 * Método que insere uma nova área no banco de dados
	 * 
	 * @param nome - nome da nova area
	 * @return long - ID da área que acaba de ser adicionada
	 */
	public long addArea(String nome, String responsavel) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NOME_AREA, nome);
		values.put(KEY_RESPONSAVEL, responsavel);

		long id = db.insert(NOME_TABELA_AREAS, null, values);

		return id;
	}

	/**
	 * Retorna o ID da area com o nome solicitado
	 * 
	 * @param nome - nome requisitada
	 * @return long - ID da area ou -1 se não existir
	 */
	public long getAreaID(String nome) {
		SQLiteDatabase db = this.getReadableDatabase();
		nome = "'" + nome + "'";
		String selectQuery = "SELECT * FROM " + NOME_TABELA_AREAS + " WHERE " + KEY_NOME_AREA + " LIKE " + nome;

		Log.i("BancoDados => getAreaID", selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			return c.getLong(c.getColumnIndex(KEY_ID_AREA));
		} else
			return -1;
	}

	/**
	 * Retorna nome da area com o ID solicitado
	 * 
	 * @param id - id requisitado
	 * @return String - nome da area solicitada ou null se não existir
	 */
	public String getAreaNome(long id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String idArea = "'" + id + "'";
		String selectQuery = "SELECT * FROM " + NOME_TABELA_AREAS + " WHERE " + KEY_ID_AREA + " = " + idArea;

		Log.i("BancoDados => getAreaNome", selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			return c.getString(c.getColumnIndex(KEY_NOME_AREA));
		} else
			return null;
	}

	/**
	 * Retorna todos os nomes das areas presentes na tabela Areas
	 * 
	 * @return List<String> - Lista contendo tadas as areas do banco de dados
	 */
	public List<String> getTodasAreas() {
		List<String> todasAreas = new ArrayList<String>();
		String selectQuery = "SELECT * FROM " + NOME_TABELA_AREAS;

		Log.i("BancoDados => getTodasAreas", selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				todasAreas.add(c.getString(c.getColumnIndex(KEY_NOME_AREA)));
			} while (c.moveToNext());
		}
		c.close();
		return todasAreas;
	}

	/**
	 * Retorna a quantidade de areas presentes na tabela Areas
	 * 
	 * @return int - Quantidades de Areas no banco de dados
	 */
	public int getQuantidadeAreas() {
		String countQuery = "SELECT * FROM " + NOME_TABELA_AREAS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	public void limpaAreas() {
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_AREAS);
		db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_LOCAIS);
		db.execSQL(CRIA_TABELA_AREAS);
		db.execSQL(CRIA_TABELA_LOCAIS);
	}
	
	public void limpaNos() {
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + NOME_TABELA_NOS);
		db.execSQL(CRIA_TABELA_LOC_POINTS);
	}
	
	// MÉTODOS RELATIVOS A LOCAIS

	/**
	 * Método que insere um novo local no banco de dados
	 * 
	 * @param local - instância do local que irá ser adicionado
	 * @return long - ID do local que acaba de ser adicionado
	 */
	public long addLocal(Local local, long idLocal, long idArea) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID_LOCAL, idLocal);
		values.put(KEY_NOME_LOCAL, local.getNome());
		values.put(KEY_LATITUDE, local.getLatitude());
		values.put(KEY_LONGITUDE, local.getLongitude());
		values.put(KEY_ALTITUDE, local.getAltitude());
		values.put(KEY_DESCRICAO_LOCAL, local.getDescricao());
		values.put(KEY_ID_AREA_LOCAL, idArea);

		long id = db.insert(NOME_TABELA_LOCAIS, null, values);

		return id;
	}

	/*
	 * get single
	 */
	public Local getLocal(String nome) {
		SQLiteDatabase db = this.getReadableDatabase();
		nome = "'" + nome + "'";
		String selectQuery = "SELECT * FROM " + NOME_TABELA_LOCAIS + " WHERE " + KEY_NOME_LOCAL + " LIKE " + nome;

		Log.i("BancoDados => getLocal", selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);
		Local local = new Local();
		if (c.moveToFirst()) {

			local.setId(c.getInt(c.getColumnIndex(KEY_ID_LOCAL)));
			local.setId(c.getInt(c.getColumnIndex(KEY_NOME_LOCAL)));
			local.setLatitude(c.getString(c.getColumnIndex(KEY_LATITUDE)));
			local.setLongitude(c.getString(c.getColumnIndex(KEY_LONGITUDE)));
			local.setAltitude(c.getString(c.getColumnIndex(KEY_ALTITUDE)));
			local.setDescricao(c.getString(c.getColumnIndex(KEY_DESCRICAO_LOCAL)));
		}
		return local;
	}

	public String getLocalPorID(long id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String idLocal = "'" + id + "'";
		String selectQuery = "SELECT * FROM " + NOME_TABELA_LOCAIS + " WHERE " + KEY_ID_LOCAL + " = " + idLocal;

		Log.i("BancoDados => getLocalPorID", selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			return c.getString(c.getColumnIndex(KEY_NOME_LOCAL));
		} else
			return null;
	}

	/**
	 * getting all
	 * */
	public List<Local> getAllLocals() {
		List<Local> locals = new ArrayList<Local>();
		String selectQuery = "SELECT * FROM " + NOME_TABELA_LOCAIS;

		Log.i("BancoDados => getAllLocals", selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Local local = new Local();
				local.setId(c.getInt(c.getColumnIndex(KEY_ID_LOCAL)));
				local.setId(c.getInt(c.getColumnIndex(KEY_NOME_LOCAL)));
				local.setLatitude(c.getString(c.getColumnIndex(KEY_LATITUDE)));
				local.setLongitude(c.getString(c.getColumnIndex(KEY_LONGITUDE)));
				local.setAltitude(c.getString(c.getColumnIndex(KEY_ALTITUDE)));
				local.setDescricao(c.getString(c.getColumnIndex(KEY_DESCRICAO_LOCAL)));

				// adding to list
				locals.add(local);
			} while (c.moveToNext());
		}

		return locals;
	}

	/**
	 * Retorna a quantidade de locais presentes na tabela Locais
	 * 
	 * @return int - Quantidades de locais no banco de dados
	 */
	public int getQuantidadeLocais() {
		String countQuery = "SELECT  * FROM " + NOME_TABELA_LOCAIS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	/**
	 * Retorna o nome do local do ID passado
	 * 
	 * @return String - Nome do local
	 */
	public String getNomeLocal(int id) {
		String idtemp = "'" + id + "'";
		String countQuery = "SELECT * FROM " + NOME_TABELA_LOCAIS + " WHERE " + KEY_ID_LOCAL + " = " + idtemp;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		// int count = cursor.getCount();
		// cursor.close();

		return cursor.getString(1);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public void menorCaminhoTeste(LatLng coordAtual, LatLng coordDestino) {
		SQLiteDatabase db = this.getReadableDatabase();
		Log.i("ENCONTRA_NO", "Latitude => " + String.valueOf(coordAtual.latitude) + " Longitude => " + String.valueOf(coordAtual.longitude));
		
		
		
		
		
		
//		String selectQueryFinal = "SELECT * FROM " + NOME_TABELA_NOS;
//		Cursor cursor = db.rawQuery(selectQueryFinal, null);
//
//		if (cursor.moveToFirst()) {
//			String lat = cursor.getString(cursor.getColumnIndex(KEY_LAT_NO));
//			String lng = cursor.getString(cursor.getColumnIndex(KEY_LNG_NO));
//			Log.i("ENCONTRA_NO", "latitude primeiro no: " + lat);
//			Log.i("ENCONTRA_NO", "longitude primeiro no: " + lng);
//		}
		
		
		
		
		
		//int id_inicial = encontraNo(db, coordAtual);
		//int id_final = encontraNo(db, coordDestino);

		
		//Log.i("ENCONTRA_NO", "id_inicial => " + String.valueOf(id_inicial));
		//Log.i("ENCONTRA_NO", "id_final   => " + String.valueOf(id_final));

	}
	
	
	
	private long encontraNo(SQLiteDatabase db, LatLng centro) {
		long id = -1L;
		double raio = 0.00001;
		// final double mult = 1.1;

		Log.i("ENCONTRA_NO", "Iniciando a busca por ponto mais proximo...");

		while (true) {
			// PointF center = new PointF((float) coordenada.latitude, (float)
			// coordenada.longitude);
			LatLng p1 = calculateDerivedPosition(centro, raio, 0);
			LatLng p2 = calculateDerivedPosition(centro, raio, 90);
			LatLng p3 = calculateDerivedPosition(centro, raio, 180);
			LatLng p4 = calculateDerivedPosition(centro, raio, 270);

			String strWhere = " WHERE " + "latitude > " + String.valueOf(p3.latitude) + " AND " + 
										  "latitude < " + String.valueOf(p1.latitude) + " AND " + 
										  "longitude < " + String.valueOf(p2.longitude) + " AND " + 
										  "longitude > " + String.valueOf(p4.longitude);

			String selectQueryFinal = "SELECT * FROM " + NOME_TABELA_NOS + strWhere;
			Cursor cursor = db.rawQuery(selectQueryFinal, null);

			
			
			
//			if(cursor.moveToFirst()){
//				id = cursor.getLong(cursor.getColumnIndex(KEY_ID_NO));
//				double x = cursor.getDouble(cursor.getColumnIndex(KEY_LAT_NO));
//				double y = cursor.getDouble(cursor.getColumnIndex(KEY_LNG_NO));
//				double dist = Math.abs(centro.latitude - x) + Math.abs(centro.longitude - y);
//				double menorDistancia = dist;
//				
//				while(cursor.moveToNext()){
//					x = cursor.getDouble(cursor.getColumnIndex(KEY_LAT_NO));
//					y = cursor.getDouble(cursor.getColumnIndex(KEY_LNG_NO));
//					dist = Math.abs(centro.latitude - x) + Math.abs(centro.longitude - y);
//					if(dist < menorDistancia){
//						id = cursor.getLong(cursor.getColumnIndex(KEY_ID_NO));
//						menorDistancia = dist;
//					}
//				}
//
//				cursor.close();
//				return id;
//			}
//			

		

			
			if (cursor.moveToFirst()) {
				// TODO ENCONTRAR A MENOR DISTANCIA!
				Log.i("ENCONTRA_NO", " ACHOU !!!");
				

					id = cursor.getLong(cursor.getColumnIndex(KEY_ID_NO));
				
				cursor.close();
				return id;
			}
			
			
			raio += 0.00001;
			//Log.i("ENCONTRA_NO", "\n Valor do raio : " + String.valueOf(raio));
			
			
			Log.i("ENCONTRA_NO", " Valor 0 : " + String.valueOf(p1.latitude));
			Log.i("ENCONTRA_NO", " Valor 90 : " + String.valueOf(p2.longitude));
			Log.i("ENCONTRA_NO", " Valor 180 : " + String.valueOf(p3.latitude));
			Log.i("ENCONTRA_NO", " Valor 270 : " + String.valueOf(p4.longitude));
			
		}
	}
	
	public static LatLng calculateDerivedPosition(LatLng centro, double range, int bearing) {

		LatLng ponto_extremo = null;
		switch (bearing) {
		case 0:
			ponto_extremo = new LatLng(centro.latitude + range, centro.longitude);
			break;
		case 180:
			ponto_extremo = new LatLng(centro.latitude - range, centro.longitude);
			break;
		case 90:
			ponto_extremo = new LatLng(centro.latitude, centro.longitude + range);
			break;
		case 270:
			ponto_extremo = new LatLng(centro.latitude, centro.longitude - range);
			break;

		}
		return ponto_extremo;

	}
	

	
	
	public PolylineOptions menorCaminho(LatLng coordAtual, LatLng coordDestino) {
		PolylineOptions caminho = new PolylineOptions();
		SQLiteDatabase db = this.getReadableDatabase();
		
		
		
		
		

		// ENCONTRA NO INICIAL E FINAL

		long id_inicial = encontraNo(db, coordAtual);
		long id_final = encontraNo(db, coordDestino);
		
//		Log.i("AStar", "No inicial: " + id_inicial);
//		Log.i("AStar", "No final: " + id_final);
		
		
		String query = "SELECT * FROM " + "nos" + " WHERE " + "_id" + " = " + String.valueOf(id_inicial);
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		
		Node origin = new Node(String.valueOf(id_inicial),cursor.getDouble(cursor.getColumnIndex("latitude")),cursor.getDouble(cursor.getColumnIndex("longitude")));
		cursor.close();
		

		
		
		query = "SELECT * FROM " + "nos" + " WHERE " + "_id" + " = " + String.valueOf(id_final);
		cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		
		Node goal = new Node(String.valueOf(id_final),cursor.getDouble(cursor.getColumnIndex("latitude")),cursor.getDouble(cursor.getColumnIndex("longitude")));
		cursor.close();


		
		

		// BUSCA
		
		caminho.add(coordDestino);
		caminho.addAll(AStar.aStar(db,origin,goal));
		caminho.add(coordAtual);
		
		
		caminho.width(4);
		caminho.color(Color.RED);
		
		

		return caminho;
	}
	






}
