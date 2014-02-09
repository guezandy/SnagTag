package com.snag.snagtag;

/*
 * Important notes on NFC the Samgsung Galaxy S4 is not compatible with the classic Mifare Tags because it 
 * uses the Broadcom NFC chip.
 * NTAG203 tags are fully compatible with all nfc phones including the galaxy s4
 */



import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.view.Menu;
import com.kinvey.android.Client;
import com.kinvey.java.core.KinveyClientCallback;

@SuppressLint("ShowToast")
@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public class ZipActivity extends BaseSherlockeFragmentActivity {
	private static final String KINVEY_KEY = "kid_PVAtuuzi2f";
	private static final String KINVEY_SECRET_KEY = "2cab4a07424945e981478fcfc02341af";
	public static final String ENTITY_DELIM = "|";
	public static final String DETAIL_DELIM = "~";
	public static final String CURRENT_SNAG = "current";
	
	private NfcAdapter mNfcAdapter;
	private Button mEnableWriteButton;
	private EditText mTextField;
	private ProgressBar mProgressBar;
	public Button backB;

	@SuppressLint("CutPasteId")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zip);
		SharedPreferences prefs = this.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
		InternalData.addItem(prefs, CURRENT_SNAG, "");
		backB = (Button) findViewById(R.id.zipBack);
		backB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ZipActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
		
		
		mTextField = (EditText) findViewById(R.id.nfcWriteString);
		mProgressBar = (ProgressBar) findViewById(R.id.pbWriteToTag);
		mProgressBar.setVisibility(View.GONE);
		
		mEnableWriteButton = (Button) findViewById(R.id.writeToTagButton);
		
		mEnableWriteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTagWriteReady(!isWriteReady);
				mProgressBar.setVisibility(isWriteReady ? View.VISIBLE : View.GONE);
			}
		});
		
		
		Button purchaseButton = (Button) findViewById(R.id.purchaseButton);
		
		purchaseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makevenmopayment("1");
			}
		});
		
		Button favbutton = (Button) findViewById(R.id.purchaseButton);
		favbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addEntity(v);
			}
		});
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) {
			Toast.makeText(this, "Sorry, NFC is not available on this device", Toast.LENGTH_SHORT).show();
			finish();
		}
		
	}
	public String app_id = "1528";
	public String app_name= "SnagTag";
	public String recipient= "7863570943";
	//public String amount;
	public String txn = "pay";
	public String note = "Bought something with SnagTag!";
    public void makevenmopayment(String amount){
        try {
            Intent venmoIntent = VenmoLibrary.openVenmoPayment(app_id, app_name, recipient, amount, note, txn);
            startActivityForResult(venmoIntent, 1); //1 is the requestCode we are using for Venmo. Feel free to change this to another number. 
        }
        catch (android.content.ActivityNotFoundException e) //Venmo native app not install on device, so let's instead open a mobile web version of Venmo in a WebView
        {
            Intent venmoIntent = new Intent(ZipActivity.this, VenmoWebViewActivity.class);
            String venmo_uri = VenmoLibrary.openVenmoPaymentInWebView(app_id, app_name, recipient, amount, note, txn);
            venmoIntent.putExtra("url", venmo_uri);
            startActivityForResult(venmoIntent, 1);
        }
    }
	
	private boolean isWriteReady = false;

	/**
	 * Enable this activity to write to a tag
	 * 
	 * @param isWriteReady
	 */
	public void setTagWriteReady(boolean isWriteReady) {
		this.isWriteReady = isWriteReady;
		if (isWriteReady) {
			IntentFilter[] writeTagFilters = new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED) };
			mNfcAdapter.enableForegroundDispatch(ZipActivity.this, NfcUtils.getPendingIntent(ZipActivity.this),
					writeTagFilters, null);
		} else {
			// Disable dispatch if not writing tags
			mNfcAdapter.disableForegroundDispatch(ZipActivity.this);
		}
		mProgressBar.setVisibility(isWriteReady ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		setIntent(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		Bundle bundle = getIntent().getExtras();
		if (isWriteReady && NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction())) {
			processWriteIntent(getIntent());
		} else if (!isWriteReady
				&& (NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction()) || NfcAdapter.ACTION_NDEF_DISCOVERED
						.equals(getIntent().getAction()))) {
			mTextField.setVisibility(View.GONE);
			mEnableWriteButton.setVisibility(View.GONE);
			processReadIntent(getIntent());
		} else if(bundle.getString("nfcId")!=null) {
			Client kinveyClient = new Client.Builder(KINVEY_KEY, KINVEY_SECRET_KEY, this).build();
			getEntity(bundle.getString("nfcId"), KINVEY_ENTITY_COLLECTION_KEY, KINVEY_UPDATE_ZIP_ACTIVITY_CASE,kinveyClient);
			mTextField.setVisibility(View.GONE);
			mEnableWriteButton.setVisibility(View.GONE);
		}
	}

	private static final String MIME_TYPE = "application/com.tapped.nfc.tag";

	/**
	 * Write to an NFC tag; reacting to an intent generated from foreground
	 * dispatch requesting a write
	 * 
	 * @param intent
	 */
	public void processWriteIntent(Intent intent) {
		if (isWriteReady && NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction())) {

			Tag detectedTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);

			String tagWriteMessage = mTextField.getText().toString();
			byte[] payload = new String(tagWriteMessage).getBytes();

			if (detectedTag != null && NfcUtils.writeTag(
					NfcUtils.createMessage(MIME_TYPE, payload), detectedTag)) {
				
				Toast.makeText(this, "Wrote '" + tagWriteMessage + "' to a tag!", 
						Toast.LENGTH_LONG).show();
				setTagWriteReady(false);
			} else {
				Toast.makeText(this, "Write failed. Please try again.", Toast.LENGTH_LONG).show();
			}
		}
	}

	public void processReadIntent(Intent intent) {
		List<NdefMessage> intentMessages = NfcUtils.getMessagesFromIntent(intent);
		List<String> payloadStrings = new ArrayList<String>(intentMessages.size());

		for (NdefMessage message : intentMessages) {
			for (NdefRecord record : message.getRecords()) {
				byte[] payload = record.getPayload();
				String payloadString = new String(payload);

				if (!TextUtils.isEmpty(payloadString))
					payloadStrings.add(payloadString);
			}
		}

		if (!payloadStrings.isEmpty()) {
			String content =  TextUtils.join(",", payloadStrings);
			Toast.makeText(ZipActivity.this, "Read from tag: " + content,
					Toast.LENGTH_LONG).show();
			System.out.println(content);
			Client kinveyClient = new Client.Builder(KINVEY_KEY, KINVEY_SECRET_KEY, this).build();
			getEntity(content, KINVEY_ENTITY_COLLECTION_KEY, KINVEY_UPDATE_ZIP_ACTIVITY_CASE,kinveyClient);
		}
	}
		public void addEntity(View v){
			SharedPreferences prefs = this.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
			String report = prefs.getString(CURRENT_SNAG, "");
	        //InternalData.addItem(prefs, CART_KEY, "Lacoste~$89.5~http://slimages.macys.com/is/image/MCY/products/8/optimized/1242258_fpx.tif|");

			if( !report.isEmpty()){
				InternalData.addItem(prefs, InternalData.CART_KEY, report);
				System.out.println("Just added to internal data");
			}
		
	}
		

		@Override 
		public boolean onCreateOptionsMenu(Menu menu){
			com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
			inflater.inflate(R.menu.zip,  (com.actionbarsherlock.view.Menu) menu);
			return super.onCreateOptionsMenu(menu);
		}	
}
