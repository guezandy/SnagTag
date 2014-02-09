package com.snag.snagtag;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.kinvey.android.Client;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainMenu extends BaseSherlockeFragmentActivity {
	protected Client kinveyClient;
	public static final String appKey = "kid_PVAtuuzi2f";
	public static final String appSecret = "2cab4a07424945e981478fcfc02341af";
	public static Context appContext;
	
	SparseArray<Group> groups = new SparseArray<Group>();
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main_menu);
		appContext = getApplicationContext();
		kinveyClient = new Client.Builder(appKey, appSecret
			    , this.getApplicationContext()).build();
	    createData();
	    ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView3);
	    if (listView == null) { Log.w("", "TextView is null"); }
	    ListAdapterMainMenu adapter = new ListAdapterMainMenu(this,
	        groups);
	    listView.setAdapter(adapter);
	    
		Button zipButton = (Button) findViewById(R.id.zipButtonAct);
		zipButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent goToZip = new Intent(MainMenu.this, ZipActivity.class);
				goToZip.putExtra("nfcId", "n");
				startActivity(goToZip);
			}
		});

	  }
/*		@Override
		public void onResume() {
			super.onResume();
			Bundle bundle = getIntent().getExtras();
			//if(bundle.getString("openstore")!=null) {
				//Client kinveyClient = new Client.Builder(KINVEY_KEY, KINVEY_SECRET_KEY, this).build();
				//getEntity(bundle.getString("nfcId"), KINVEY_ENTITY_COLLECTION_KEY, KINVEY_UPDATE_ZIP_ACTIVITY_CASE,kinveyClient);
				//mTextField.setVisibility(View.GONE);
				//mEnableWriteButton.setVisibility(View.GONE);
				
			//}
		}*/

	public String[] grouplist4 =  {"Vince Camuto", "Theory", "Lulu Lemon","Gap", "American Apparel", "Salvatore Ferragamo", "J Crew","Express"};
	
	Store s1 = new Store("Vince Camuto","http://www.vincecamuto.com/");
	Store s2 = new Store("Theory","http://www.theory.com/");
	Store s3 = new Store("Lulu Lemon","http://shop.lululemon.com/home.jsp");
	Store s4 = new Store("Gap","http://www.gap.com/");
	Store s5 = new Store("American Apparel","http://www.americanapparel.net/");
	Store s6 = new Store("Salvatore Ferragamo","http://www.ferragamo.com/shop/en/usa");
	Store s7 = new Store("J Crew","http://www.jcrew.com/index.jsp");
	Store s8 = new Store("Express","http://www.express.com/");
	ArrayList<Store> storesx = new ArrayList<Store>();
	
	public void populatelists2(){
		storesx.add(s1);
		storesx.add(s2);
		storesx.add(s3);
		storesx.add(s4);
		storesx.add(s5);
		storesx.add(s6);
		storesx.add(s7);
		storesx.add(s8);
	}
	  public void createData() {
		 populatelists2();
	    for (int j = 0; j < 8; j++) {
	      Group group = new Group(storesx.get(j).getStorename(),storesx.get(j).getStoreurl());
	      for (int i = 0; i < 2; i++) {
	        if(i==0){
	        	group.children.add("Name");
	        }
	        else{
	        	group.children.add("Url");
	        }
	      }
	      groups.append(j, group);
	    }
	  }
	  
	  

	
	
	
	
/*	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		appContext = getApplicationContext();

		
		kinveyClient = new Client.Builder(appKey, appSecret
			    , this.getApplicationContext()).build();*/
		/*
		Button goToCloset = (Button) findViewById(R.id.goToClosetButton);
		goToCloset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainMenu.this, GoToClosetActivity.class));
			}
		});
		*/
//		Button zipButton = (Button) findViewById(R.id.zipButtonAct);
//		zipButton.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent goToZip = new Intent(MainMenu.this, ZipActivity.class);
//				goToZip.putExtra("nfcId", "n");
//				startActivity(goToZip);
//			}
//		});
		
		/*
		Button tagHistoryButton = (Button) findViewById(R.id.zipButton);
		tagHistoryButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainMenu.this, TagsHistoryActivity.class));
			}
		});
		/*
SUPEEEERRR IMPORTANT LOGOUT BUTTON ON ACTION BAR!!!
		Button logout = (Button) findViewById(R.id.logout);
		logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				kinveyClient.user().logout().execute();
				startActivity(new Intent(MainMenu.this, MainActivity.class));
			}
		});
		*/
	//}
	
	@Override 
	public boolean onCreateOptionsMenu(Menu menu){
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main,  (com.actionbarsherlock.view.Menu) menu);
		return super.onCreateOptionsMenu(menu);
	}

		
	    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
	    protected void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
	    }
}

//class MyTabsListener implements ActionBar.TabListener {
//	public Fragment fragment;
//
//	public MyTabsListener(Fragment fragment) {
//		this.fragment = fragment;
//	}
//
//	@Override
//	public void onTabReselected(Tab tab, FragmentTransaction ft) {
//		Toast.makeText(MainMenu.appContext, "Reselected!", Toast.LENGTH_LONG).show();
//	}
//
//	@Override
//	public void onTabSelected(Tab tab, FragmentTransaction ft) {
//		ft.replace(R.id.fragment_container, fragment);
//	}
//
//	@Override
//	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
//		ft.remove(fragment);
//	}
//}


