package com.example.localizador;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Node {
	//List<Node> neighbors = new ArrayList<Node>();
	Node parent;
	double f; 	// f = g + h
	double g;	// é o custo do movimento para se mover do ponto de início até o quadrado determinado na malha seguindo o caminho criado para chegar lá.
	double h;	// custo deste nó até o nó destino (Heuristica)
	double x;
	double y;
	//int cost;
	String id;
	
	public List<Node> getNeighbors(SQLiteDatabase db) {
		List<Node> neighbors = new ArrayList<Node>();
		
		String queryNeighbors = "SELECT * FROM " + "nos" + " WHERE " + "_id" + " = " + id;
		Cursor cursorNeighbors = db.rawQuery(queryNeighbors, null);
		cursorNeighbors.moveToFirst();
		String[] ligacoes = cursorNeighbors.getString(cursorNeighbors.getColumnIndex("liga")).split(" ");
		cursorNeighbors.close();
		
		//Log.i("AStar", "Pegando vizinhos de: " + id);
		
		for (String ligacao_atual : ligacoes) {
			
			Log.i("AStar", "Vizinho: " + ligacao_atual);
			
			String query = "SELECT * FROM " + "nos" + " WHERE " + "_id" + " = " + ligacao_atual;
			Cursor cursor = db.rawQuery(query, null);
			cursor.moveToFirst();
			
			
			Node node = new Node();
			node.id = ligacao_atual;
			node.x = cursor.getDouble(cursor.getColumnIndex("latitude"));
			node.y= cursor.getDouble(cursor.getColumnIndex("longitude"));
			cursor.close();
			neighbors.add(node);
		}
		
		return neighbors;
	}
	
	public Node(){}
	
	public Node(String id, double x, double y){
		this.id = id;
		this.x = x;
		this.y=y;
	}
	
	@Override
	public int hashCode(){
		return Integer.valueOf(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Node))
			return false;	
		if (obj == this)
			return true;
		return this.id.equals(((Node) obj).id);
	}
	
}
