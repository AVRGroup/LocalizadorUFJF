package com.example.localizador;

import java.util.Comparator;

public class Local implements Comparable<Local> {

	public int id;

	public String nome;
	public String latitude;
	public String longitude;
	public String altitude;
	public String descricao;

	public Local() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getAltitude() {
		return altitude;
	}

	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public int compareTo(Local outroLugar) {
		return nome.compareTo(outroLugar.getNome());
	}

	public static double distancia(Local local1, Local local2) {
		double x1 = Double.valueOf(local1.getLatitude());
		double y1 = Double.valueOf(local2.getLongitude());
		double x2 = Double.valueOf(local2.getLatitude());
		double y2 = Double.valueOf(local2.getLongitude());
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
	
	public static double distancia(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

}
