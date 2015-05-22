package com.example.localizador;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.Camera;
import android.location.Location;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.SearchView.OnSuggestionListener;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	
    
	private Camera camera;
	private TelaCamera telaCam;
	private MyRender render;
	private SensorToOpenGl sensorToOpenGl;
	private GoogleMap mapa;
	private Menu menu;
	private FrameLayout preview;

	private MenuItem buscaItem;
	private MenuItem cameraItem;
	private MenuItem mapItem;

	private LinearLayout mapaLayout;
	private LinearLayout cameraLayout;

	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private String[] itensMenu;
	private ListView mDrawerList;

	private BancoDados banco;

	private static boolean captura = true;
	private boolean mUpdatesRequested = true;

	private LocationRequest mLocationRequest;

	private float precLimite = 40.0f;
	private MarkerOptions marker;
	private MarkerOptions lugarBuscado;
	private static LocationClient mLocationClient;
	static Location mCurrentLocation;

	private LatLng coordAtual;
	private LatLng coordDestino;
	private PolylineOptions caminho;
	private float[] pontosCartesianos;
	private GLSurfaceView glView;
	
	public static Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		
		context = this;
		
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		mLocationRequest.setInterval(1000 * 2); // Milissegundos por segundo x
													// segundos - 1000 * 60
		mLocationRequest.setFastestInterval(1000 * 2);
		//mLocationRequest.setExpirationDuration(2 * 1000);

		mLocationClient = new LocationClient(this, this, this);

		Localiza();

		setContentView(R.layout.activity_main);

		// Inicia Banco de Dados

		banco = new BancoDados(this);
		
//		GerenciadorBanco gerenciadorBanco = new GerenciadorBanco(this);
//		gerenciadorBanco.inicializaBancos(banco);
//		gerenciadorBanco.inicializaNos(banco);

		
		// Carrega Menu

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		itensMenu = getResources().getStringArray(R.array.itensMenu);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.item_menu, itensMenu));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		iniciaMenuBandeja();

		// Carrega Camera:

		
	    glView = new GLSurfaceView( this );
	    glView.setEGLConfigChooser( 8, 8, 8, 8, 16, 0 );
	    glView.getHolder().setFormat( PixelFormat.TRANSLUCENT );
	    render = new MyRender();
	    sensorToOpenGl = new SensorToOpenGl(context);
	    glView.setRenderer( sensorToOpenGl );
	    
		
		cameraLayout = (LinearLayout) findViewById(R.id.layoutCamera);
		camera = Camera.open();
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		telaCam = new TelaCamera(this, camera, display);
		preview = (FrameLayout) findViewById(R.id.camera);
		preview.addView(glView);
		preview.addView(telaCam);

		// Carrega Mapa:

		mapaLayout = (LinearLayout) findViewById(R.id.layoutMapa);
		if (mapa == null) {
			mapa = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		}
		mapa.getUiSettings().setZoomControlsEnabled(false);
		mapa.getUiSettings().setCompassEnabled(false);
		mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-21.77673708, -43.36914182), 14.85f));

		
		
		marker = new MarkerOptions();
		marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
		marker.title("Estou Aqui!");

		// mapa.setOnMapClickListener(new OnMapClickListener() {
		//
		// @Override
		// public void onMapClick(LatLng arg0) {
		// if (coordAtual != null) {
		// mapa.clear();
		// marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
		// mapa.addMarker(marker);
		// }
		// if (coordDestino != null) {
		// mapa.addMarker(lugarBuscado);
		// }
		// }
		// });

		// mapa.setOnMarkerClickListener(new OnMarkerClickListener() {
		//
		// @Override
		// public boolean onMarkerClick(Marker clicado) {
		// if (clicado.equals(estouAqui)) {
		// marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_up));
		// mapa.clear();
		// mapa.addMarker(marker);
		//
		// } else if (coordAtual != null) {
		// mapa.clear();
		// marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
		// mapa.addMarker(marker);
		// }
		//
		// if (coordDestino != null) {
		// mapa.addMarker(lugarBuscado);
		// }
		//
		// return false;
		// }
		//
		// });

		lugarBuscado = new MarkerOptions();
		
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		this.menu = menu;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		buscaItem = menu.findItem(R.id.action_search);
		cameraItem = menu.findItem(R.id.action_cam);
		mapItem = menu.findItem(R.id.action_map);

		final SearchView busca = (SearchView) MenuItemCompat.getActionView(buscaItem);

		busca.setOnSearchClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cameraItem = menu.findItem(R.id.action_cam).setVisible(false).setEnabled(false);
				mapItem = menu.findItem(R.id.action_map).setVisible(false).setEnabled(false);
				mapItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
				cameraItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			}
		});

		busca.setOnCloseListener(new OnCloseListener() {

			@Override
			public boolean onClose() {
				cameraItem = menu.findItem(R.id.action_cam).setVisible(true).setEnabled(true);
				mapItem = menu.findItem(R.id.action_map).setVisible(true).setEnabled(true);
				mapItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
				cameraItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
				return false;
			}
		});

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		busca.setQueryHint("Onde?");
		busca.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		busca.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String query) {

				List<String> resultados;
				Local local = null;	
//				Local local = new Local();
//				local.setLatitude(String.valueOf(coordAtual.latitude));
//				local.setLongitude(String.valueOf(coordAtual.longitude));
				resultados = banco.busca(query, local);

				Cursor cursor = banco.getCursor(query);

				SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

				final SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

				search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

				final BuscaAdapter buscaAdapter = new BuscaAdapter(getApplicationContext(), cursor, resultados);

				search.setSuggestionsAdapter(buscaAdapter);
				search.setOnSuggestionListener(new OnSuggestionListener() {
	
					@Override
					public boolean onSuggestionClick(int position) {
						mapa.clear();
						
						if (coordAtual != null) {
							mapa.addMarker(marker);
						}

						Cursor c = (Cursor) buscaAdapter.getItem(position);
						coordDestino = new LatLng(Double.valueOf(c.getString(c.getColumnIndex("latitude"))), Double.valueOf(c.getString(c.getColumnIndex("longitude"))));
						lugarBuscado.title(c.getString(c.getColumnIndex("nome_local")));
						lugarBuscado.position(new LatLng(Double.valueOf(c.getString(c.getColumnIndex("latitude"))), Double.valueOf(c.getString(c.getColumnIndex("longitude")))));

						
						//TODO REMOVA DEPOIS
						//coordAtual = new LatLng(-21.775782, -43.372170);
											
						
						
						if (coordAtual != null) {
							//caminho = banco.menorCaminho3(coordAtual, coordDestino);
							//mapa.addPolyline(caminho);
							
							caminho = banco.menorCaminho(coordAtual, coordDestino);
							mapa.addPolyline(caminho);
							//TODO metodo desenhar
							converteCoordenadas(caminho.getPoints());
							//sensorToOpenGl.setPoints(pontosCartesianos);
							//render.setPoints(pontosCartesianos);
							
						}
						
						mapa.addMarker(lugarBuscado).showInfoWindow();
						mapa.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(c.getString(c.getColumnIndex("latitude"))), Double.valueOf(c.getString(c.getColumnIndex("longitude"))))));
						

						
						
						//Toast.makeText(getApplicationContext(), String.valueOf(c.getString(c.getColumnIndex("latitude")).getBytes().length) , Toast.LENGTH_LONG).show();

						busca.onActionViewCollapsed();
						busca.setQuery("", false);
						busca.clearFocus();
						cameraItem = menu.findItem(R.id.action_cam).setVisible(true).setEnabled(true);
						mapItem = menu.findItem(R.id.action_map).setVisible(true).setEnabled(true);
						mapItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
						cameraItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

						return false;
					}

					@Override
					public boolean onSuggestionSelect(int position) {
						return false;
					}
				});

				return true;

			}

			@Override
			public boolean onQueryTextSubmit(String lugar) {
				Local resposta = banco.buscaExata(lugar);
				if (resposta != null) {
					lugarBuscado = new MarkerOptions();
					lugarBuscado.title(resposta.getNome());
					lugarBuscado.position(new LatLng(Double.valueOf(resposta.getLatitude()), Double.valueOf(resposta.getLongitude())));
					mapa.addMarker(lugarBuscado).showInfoWindow();
					mapa.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(resposta.getLatitude()), Double.valueOf(resposta.getLongitude()))));

					busca.onActionViewCollapsed();
					busca.setQuery("", false);
					busca.clearFocus();
					cameraItem = menu.findItem(R.id.action_cam).setVisible(true).setEnabled(true);
					mapItem = menu.findItem(R.id.action_map).setVisible(true).setEnabled(true);
					mapItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
					cameraItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

				}

				return false;
			}

		});

		// When using the support library, the setOnActionExpandListener()
		// method is
		// static and accepts the MenuItem object as an argument
		MenuItemCompat.setOnActionExpandListener(buscaItem, new OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				// Do something when collapsed
				return true; // Return true to collapse action view
			}

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				// Do something when expanded
				return true; // Return true to expand action view
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {

		case R.id.action_map:
			mapItem.setIcon(R.drawable.ic_action_location_selected);
			cameraItem.setIcon(R.drawable.ic_action_camera);
			cameraLayout.setVisibility(View.GONE);
			cameraLayout.invalidate();
			mapaLayout.setVisibility(View.VISIBLE);
			return true;
		case R.id.action_cam:
			cameraItem.setIcon(R.drawable.ic_action_camera_selected);
			mapItem.setIcon(R.drawable.ic_action_location);
			mapaLayout.setVisibility(View.GONE);
			mapaLayout.invalidate();
			cameraLayout.setVisibility(View.VISIBLE);
			preview.setVisibility(View.VISIBLE);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void iniciaMenuBandeja() {

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, /*
																		 * DrawerLayout
																		 * object
																		 */
		R.drawable.ic_navigation_drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				// invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				// invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}
		};

		// Defer code dependent on restoration of previous instance state.
		// NB: required for the drawer indicator to show up!
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			switch (position) {

			case 0:
				Toast.makeText(context, "Ainda n�o existem op��es", Toast.LENGTH_LONG);
				break;

			case 1:
				Toast.makeText(context, "Vers�o 1.0 Atualizada", Toast.LENGTH_LONG);
				break;
		
			case 2:
				mDrawerLayout.closeDrawers();
				sobre();
				break;
			
			}
		}
	}

	private void sobre() {
		AlertDialog dialogSobre;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		LayoutInflater inflater = this.getLayoutInflater();

		builder.setView(inflater.inflate(R.layout.sobre_layout, null))

		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

			}
		});

		dialogSobre = builder.create();
		dialogSobre.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		if (captura)
			Atualizar(location);
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this, 9000);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			// showErrorDialog(connectionResult.getErrorCode());
			Toast.makeText(getApplicationContext(), "ERRO FATAL", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onConnected(Bundle dataBundle) {
		if (mUpdatesRequested) {
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}
	}

	@Override
	public void onDisconnected() {

	}

	private void Atualizar(Location location) {
		double latPoint = location.getLatitude();
		double lngPoint = location.getLongitude();
		float prec = location.getAccuracy();

		if (prec <= precLimite) {
			mapa.clear();
			
			

			coordAtual = new LatLng(-21.77579058, -43.37188438);
			marker.position(new LatLng(-21.77579058, -43.37188438));
			
//			coordAtual = new LatLng(latPoint, lngPoint);
//			marker.position(new LatLng(latPoint, lngPoint));
			mapa.addMarker(marker);

			if (coordDestino != null) {
				mapa.addPolyline(caminho);
				mapa.addMarker(lugarBuscado).showInfoWindow();
			}
		}
	}

	private void Localiza() {
		if (mLocationClient.isConnected()) {
			mCurrentLocation = mLocationClient.getLastLocation();
			Atualizar(mCurrentLocation);
			captura = true;
		} else {
			mLocationClient.connect();
		}
	}
	
	public static Context getContext(){
		return context;
	}
	
	public void converteCoordenadas(List<LatLng> pontos){
//		List<LatLng> pontosCaminho = new ArrayList<LatLng>(pontos);
//		pontosCaminho.add(coordDestino);
//		pontosCaminho.add(0, coordAtual);
//		sensorToOpenGl.setPoints(pontosCaminho);
		
		int fator = -1000;
		int subtracaoLat = 21770;
		int subtracaoLng = 43370;
		
		List<LatLng> pontosCaminho = new ArrayList<LatLng>();
		
		pontosCaminho.add(new LatLng( 	((coordAtual.latitude * fator) -subtracaoLat) ,
										((coordAtual.longitude * fator) -subtracaoLng) ));
		
		for(int i = 0; i < pontos.size(); i++){
			double latitude  = ((pontos.get(i).latitude * fator) -subtracaoLat) ;
			double longitude = ((pontos.get(i).longitude * fator) -subtracaoLng) ;
			pontosCaminho.add(new LatLng(latitude, longitude));
		}

		pontosCaminho.add(new LatLng( ((coordDestino.latitude * fator) -subtracaoLat) ,
									  ((coordDestino.longitude * fator) -subtracaoLng)  ));
		
//		List<LatLng> pontosCaminho = new ArrayList<LatLng>();
//		
//		pontosCaminho.add(new LatLng( 	coordAtual.latitude * fator,
//										coordAtual.longitude * fator));
//		
//		for(int i = 0; i < pontos.size(); i++){
//			double latitude  = (pontos.get(i).latitude - coordAtual.latitude) * fator;
//			double longitude = (pontos.get(i).longitude - coordAtual.longitude) * fator;
//			pontosCaminho.add(new LatLng(latitude, longitude));
//		}
//
//		pontosCaminho.add(new LatLng( (coordDestino.latitude - coordAtual.latitude) *fator, (coordDestino.longitude - coordAtual.longitude) *fator)); 
//		
//		
		
		
		
		sensorToOpenGl.setPoints2(pontosCaminho);
		
		
//		List<LatLng> pontosCaminho = new ArrayList<LatLng>();
//		
//		pontosCaminho.add(new LatLng(	6371* Math.cos(coordAtual.latitude) * Math.cos(coordAtual.longitude),
//										6371* Math.cos(coordAtual.latitude) * Math.sin(coordAtual.longitude)));
//		
//		for(int i = 0; i < pontos.size(); i++){
//			double latitude  = 6371* Math.cos(pontos.get(i).latitude) * Math.cos(pontos.get(i).longitude);
//			double longitude  = 6371* Math.cos(pontos.get(i).latitude) * Math.sin(pontos.get(i).longitude);
//			pontosCaminho.add(new LatLng(latitude, longitude));
//		}
//
//		pontosCaminho.add(new LatLng(	6371* Math.cos(coordDestino.latitude) * Math.cos(coordDestino.longitude),
//										6371* Math.cos(coordDestino.latitude) * Math.sin(coordDestino.longitude)));
//		
//		sensorToOpenGl.setPoints(pontosCaminho);
			
	}
}