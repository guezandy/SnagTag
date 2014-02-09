package com.snag.snagtag;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyDeleteCallback;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.model.KinveyDeleteResponse;

public class KinveyActivity extends FragmentActivity {
	
	public static final int KINVEY_LISTVIEW_CASE = 0;
	public static final int KINVEY_UPDATE_ZIP_ACTIVITY_CASE = 1;
	public static final int KINVEY_TAGS_HISTORY_CASE = 2;
	public final static String PACKAGE_NAME = "com.zipper.zipcloset";
	
	
	public static final String KINVEY_ENTITY_COLLECTION_KEY ="EntityCollection";
	public static final String KINVEY_FAVORITES_KEY = "Favorites";
	public static final String KINVEY_TAGS_KEY = "Tags";
	public static final String CURRENT_SNAG = "current";
	
	

	// pulls an entity from Kinvey by its id and executes specified method by methodCase
	public void getEntity(String entityId, String collection, final int methodCase,final Client kinveyClient) {
		
		final String TAG = "getEntity";
		final Context context = this.getApplicationContext();
        kinveyClient.appData(collection, Entity.class).getEntity(entityId, new KinveyClientCallback<Entity>() {
            @Override
            public void onSuccess(Entity entity) {
                if (!(entity == null)){
                	entitySwitch(methodCase,entity, kinveyClient);
                    //Toast.makeText(context,"Entity Retrieved\nTitle: " + entity.getId()
                    //+ "\nDescription: " + entity.get("Description"), Toast.LENGTH_LONG).show();
                }
                
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "AppData.getEntity Failure", error);
                Toast.makeText(context, "Get Entity error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
	
	// pulls entity[] by collection and key, and executes specified method  by methodCase
	public void queryEntities(String collection,String key, String id,final int methodCase,final ListView lv,Client kinveyClient) {
		
    	final String TAG = "Query Entities";
    	final Context context = this.getApplicationContext();
        Query myQuery = kinveyClient.query();
        myQuery.equals(key,id); //what does this do?
        kinveyClient.appData(collection, Entity.class).get(myQuery, new KinveyListCallback<Entity>() {
            @Override
            public void onSuccess(Entity[] result) {
                if (!(result == null)){
               // entitiesSwitch(methodCase, result, lv);
                for (Entity entity : result) {
                    Toast.makeText(context,"Entity Retrieved\nTitle: " + entity.getId()
                            + "\nDescription: " + entity.get("Description"), Toast.LENGTH_LONG).show();
                	}
                }
            }
	            @Override
	            public void onFailure(Throwable error) {
	                Log.e(TAG, "AppData.get by Query Failure", error);
	                Toast.makeText(context, "Get by Query error: " + error.getMessage(), Toast.LENGTH_LONG).show();
	            }
	        });
	    }
	
	
	// pulls entity[] by specified collection and executes specified method  by methodIndex case
    public void getCollection(String collection, final int methodIndex, final ListView lv, Client kinveyClient) {
    	final String TAG = "Query";
    	final Context context = this.getApplicationContext();
    	
    	Query myQuery = kinveyClient.query();
    	//myQuery.equals("Name","Launch Party");
    	AsyncAppData<Entity> myEvents = kinveyClient.appData(collection, Entity.class);
    	myEvents.get(myQuery, new KinveyListCallback<Entity>() {
            @Override
            public void onSuccess(Entity[] result) {
                if (!(result == null)){
                	ArrayList<Entity> resultlist = new ArrayList<Entity>(Arrays.asList(result));
                entitiesSwitch( methodIndex,resultlist, lv);
               
                for (Entity entity : result) {
                    Toast.makeText(context,"Entity Retrieved\nTitle: " + entity.getId()
                            + "\nDescription: " + entity.get("Brand"), Toast.LENGTH_LONG).show();
                	}
                
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "AppData.get all Failure", error);
                Toast.makeText(context, "Get All error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

	// Saves entity to specific Kinvey collection
	public void saveEntity(Entity entity, String collection, Client kinveyClient) {
		final String TAG = "Save Entity";
		final Context context = this.getApplicationContext();
        kinveyClient.appData(collection, Entity.class).save(entity, new KinveyClientCallback<Entity>() {
            @Override
            public void onSuccess(Entity result) {
                Toast.makeText(context,"Entity Saved\nTitle: " + result.getId()
                        + "\nDescription: " + result.get("Description"), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "AppData.save Failure", error);
                Toast.makeText(context, "Save All error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

	//Deletes entity fromKinvey Database
    public void deleteEntity(String entityString, String collection,Client kinveyClient) {
    	final String TAG = "Delete Entity";
    	final Context context = this.getApplicationContext();
        kinveyClient.appData(collection, Entity.class).delete(entityString, new KinveyDeleteCallback() {
            @Override
            public void onSuccess(KinveyDeleteResponse result) {
                Toast.makeText(context,"Number of Entities Deleted: " + result.getCount(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "AppData.delete Failure", error);
                Toast.makeText(context, "Delete error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
	
    // Switch statement with cases based on methodCase, and corresponding methods
    public void entitiesSwitch(int methodCase, ArrayList<Entity> entities, ListView lv){
		String tag = "Kinvey, entitiesSwitch";
		String switchCase = null;
		switch (methodCase){
		    case KINVEY_LISTVIEW_CASE :
		    	switchCase = "Update ListView";
		    	updateListView(entities,lv);
		    	break;
		    default:
		    	Log.e(tag,switchCase);
		    	break;
			}
		}
    
    // Switch statement with cases based on methodCase, and corresponding methods
	public void entitySwitch(int methodCase, Entity entity, Client kinveyClient){
		String TAG = "Kinvey, entitySwitch ";
		String switchCase = null;
		switch (methodCase){
		     case KINVEY_UPDATE_ZIP_ACTIVITY_CASE:
		    	switchCase = "KINVEY: Update ZipActivity";
		     	updateZipActivityEntity(entity, kinveyClient);
		     	break;
		     default:
		     	Log.e(TAG,switchCase);
		     	break;
		     }
		}
	
    //Populates ZipActivity with Entity's info
	public void updateZipActivityEntity(final Entity entity, final Client kinveyClient){
		//kinveyClient.push().initialize(getApplication());
		TextView idText = (TextView) findViewById(R.id.textViewId);
		TextView brand = (TextView) findViewById(R.id.textViewBrand);
		TextView price = (TextView) findViewById(R.id.textViewPrice);
	
		ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.pbWriteToTag);
		mProgressBar.setVisibility(View.GONE);
		
		//Button mPurchaseButton = (Button) findViewById(R.id.purchaseButton);
		Button mFavoriteButton = (Button) findViewById(R.id.favoriteButton);
	
		System.out.println("Inside if statement" +entity.getId());  
		idText.setText("Clothing Type: " + entity.get("Type"));
		price.setText("Price: "+entity.get("Price"));
		brand.setText("Brand: "+entity.get("Store"));
		SharedPreferences prefs = this.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
		InternalData.addItem(prefs, CURRENT_SNAG, entity.toString());
//		mPurchaseButton.setOnClickListener(new OnClickListener() {
//				
//			@Override
//			public void onClick(View v) {
//				saveEntity(entity, "Closet", kinveyClient);
//				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(entity.get("PurchaseUrl").toString()));
//				startActivity(browserIntent);
//			}
//		});
		mFavoriteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveEntity(entity, "Favorites", kinveyClient);
				Toast.makeText(getApplicationContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
				}
					});
		ImageView mImageView = (ImageView) findViewById(R.id.itemImage);
		if(mImageView != null) {
			new ImageDownloaderTask(mImageView).execute( entity.get("ImageUrl").toString());
		}
		//else  {}
		saveEntity(entity, KINVEY_TAGS_KEY, kinveyClient);
	}
	
	//updates given ListView with a list of the given entities
	public void updateListView(final ArrayList<Entity> entities, final ListView lv){
		//ListView lv2 = (ListView) findViewById(R.id.custom_list);
		//System.out.println("first Brand= "+ entities[0].get("Brand"));
		final Context context = this.getApplicationContext();
		//System.out.println("arraysize " +entities.length);
		ArrayList<Entity> arrayResults = new ArrayList<Entity>();
		for(Entity x:entities){
			arrayResults.add(x);
		}
		System.out.println("brand= "+ arrayResults.get(0).get("Brand"));
		
		
		CustomListAdapter clist=  new CustomListAdapter(context, entities);
		//  System.out.println(lv.toString());
		lv.setAdapter(clist);
		lv.setOnItemClickListener(new OnItemClickListener() {	
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				Object o = lv.getItemAtPosition(position);
				Entity closetData = (Entity) o;
				Toast.makeText(context, "Selected :" + " " + closetData,
						Toast.LENGTH_LONG).show();
					}
		});
		
	}
	
}


