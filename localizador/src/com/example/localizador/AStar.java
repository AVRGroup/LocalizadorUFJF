package com.example.localizador;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class AStar {

	public static List<LatLng> aStar(SQLiteDatabase dataBase, Node start, Node goal) {
		Set<Node> open = new HashSet<Node>(); 	// Lista aberta: nos a serem
												// verificados
		Set<Node> closed = new HashSet<Node>(); // Já verificados, não precisam
												// ser verificados novamente

		
		Node last = goal;
		
		Log.i("AStar", "No final id: " + goal.id);
		Log.i("AStar", "No final X: " + goal.x);
		Log.i("AStar", "No final Y: " + goal.y);

		start.g = 0.0;
		start.h = estimateDistance(start, goal);
		start.f = start.h;

		Log.i("AStar", "No inicial id: " + start.id);
		Log.i("AStar", "No inicial X: " + start.x);
		Log.i("AStar", "No inicial Y: " + start.y);
		Log.i("AStar", "No inicial G: " + start.g);
		Log.i("AStar", "No inicial H: " + start.h);
		Log.i("AStar", "No inicial F: " + start.f);

		Log.i("AStar", "Open size inicial: " + open.size());

		open.add(start);

		Log.i("AStar", "Open size apos primeiro add: " + open.size());
		
		Log.i("AStar", "INICIO DO LOOP ");

		while (true) {
			
			Node current = null;

			if (open.size() == 0) {
				Log.i("AStar", "NENHUMA ROTA POSSIVEL");
				break;
				//throw new RuntimeException("Nenhuma Rota");
			}

			// Escolher o no da lista aberta com o menor F
			for (Node node : open) {
				if (current == null || node.f < current.f) {
					current = node;
					Log.i("AStar", "current id: " + current.id);

				}
			}

			// Se o nó é o final, pare!
			if (current.id.equals(goal.id) || current.id == goal.id || current == goal) {
				last = current;
				break;
			}

			// Remove da lista aberta e acrescenta na lista fechada
			open.remove(current);
//			Log.i("AStar", "Remove da OPEN: " + current.id);
//			Log.i("AStar", "Adiciona na CLOSED: " + current.id);
			closed.add(current);
			
			// Confirindo todos os nos adjacentes
			List<Node> vizinhos =  current.getNeighbors(dataBase);
			for (Node neighbor : vizinhos) {
				
//				Log.i("AStar", "Processando vizinho: " + neighbor.id);
				
				// Se este nó nao tem vivizinhos pule ele
				if (neighbor == null) {
					continue;
				}
				
				//  Ignorando os que estão na lista fechada 
			 	if(closed.contains(neighbor)){
					Log.i("AStar", "Ja esta na lista fechada");
			 		continue;
				}

				double nextG = current.g + estimateDistance(current, neighbor);
				
				// Se não estiver na lista aberta, faça o nó selecionado o pai do nó vizinho e adicione na lista aberta
				if(!open.contains(neighbor)){
//					neighbor.g = nextG;
//					neighbor.h = estimateDistance(neighbor, goal);
//					neighbor.f = neighbor.g + neighbor.h;
					neighbor.parent = current;
					open.add(neighbor);
					Log.i("AStar", "teste");
				}else if(nextG < current.g ){ // se estiver na lista aberta confira para ver se este caminho para aquele quadrado for melhor
					neighbor.parent = current;
					neighbor.g = nextG;
					neighbor.h = estimateDistance(neighbor, goal);
					neighbor.f = neighbor.g + neighbor.h;
					Log.i("AStar", "HEYHEYHEYHEY");
				}
			}
		}

		List<LatLng> nodes = new ArrayList<LatLng>(); // Caminho
		
		//Node current = goal;
		
		

		
		Node current = last;
		nodes.add(new LatLng(goal.x, goal.y));
		while (current.parent != null) {

			LatLng point = new LatLng(current.x, current.y);
			nodes.add(point);

			// nodes.add(current);
			current = current.parent;
		}
		nodes.add(new LatLng(start.x, start.y));

		return nodes;
	}

	public static double estimateDistance(Node node1, Node node2) {
		return Math.abs(node1.x - node2.x) + Math.abs(node1.y - node2.y);
	}
	
	public static double estimateDistance(double lat1, double lng1, double lat2, double lng2) {
	    double earthRadius = 6371;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLng / 2);
	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
	            * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    return dist;
	    }

}
