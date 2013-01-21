package in.ganz7.heliusweather;

import in.ganz7.heliusweather.support.ColorTheme;
import in.ganz7.heliusweather.support.XMLParser;
import in.ganz7.heliusweather.util.SystemUiHider;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HeliusMainActivity extends Activity {
	
	private static final boolean AUTO_HIDE = true;
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final boolean TOGGLE_ON_CLICK = true;
	
	SharedPreferences SP;
	String currentLocation,currentUnit="10";
	String currentTemp,tempC, tempF;

	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	
	private SystemUiHider mSystemUiHider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_helius_main);
		
		setFont(); //Sets the custom fonts
        
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        currentLocation = SP.getString("location", "");
        currentUnit = SP.getString("unit", "10");
		
		
		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.heliusMain);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.settings).setOnTouchListener(
				mDelayHideTouchListener);
	}//End of onCreate

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Log.w("OnResume", "Welcome");
		
		String temp= SP.getString("location", ""),unit="";
     
        if(currentLocation == "") //Why not use currentLocation instead? YES!
        {
        	createCustomDialog().show();
        	
        }
		
        else if( !temp.equalsIgnoreCase(currentLocation)) //True if the location has been changed.
		{
			
			currentLocation = temp;     //--Modifying this in the doInBackground Function
			new AsyncWeather().execute(temp);
		}
		
		else if(temp.equalsIgnoreCase(currentLocation))
		{
		
			Calendar c = Calendar.getInstance();
			currentLocation = temp;
	        SimpleDateFormat rev = new SimpleDateFormat("yyMMddHHmm");
	        String timeNow = rev.format(c.getTime());
	        String lastUpdate = SP.getString("updateOrder", "1000000000");
	        Log.w(timeNow,lastUpdate);
	        int difference = Integer.parseInt(timeNow) - Integer.parseInt(lastUpdate);
	        	Log.w("val"+difference, "difference");
	        if( difference < 2)//Don't update if last update was 2 minutes before
	        {        	
	        		Log.w("no update!!", "lololol" );	
	        		
	        		ColorTheme theme = new ColorTheme(Integer.parseInt(SP.getString("code","")), SP.getString("condition", ""));
	   			 
	   			 
	   			 	View view = findViewById(R.id.heliusMain);
	   			 	view.setBackgroundColor(theme.returnBGColor());
	        		
	        		TextView tv3 = (TextView)findViewById(R.id.temperature);
	        		Log.w(SP.getString("unit", "10"),"balls");		//WHY THE HELL IS THIS NOT WORKING!!!! Ohh its working now.
	        		if(SP.getString("unit", "10").equals("10"))
	        		{
	        			Log.w("Yeah","Its true");
	        			tv3.setText(SP.getString("temp_C",""));
	        		}
					else
						tv3.setText(SP.getString("temp_F",""));
		   			
		   			TextView tv2 = (TextView)findViewById(R.id.condition);
		   			tv2.setText(SP.getString("condition",""));
		   			TextView tv1 = (TextView)findViewById(R.id.location);
		   			tv1.setText(currentLocation);
	        }
	        else
	        	new AsyncWeather().execute(temp);
		}
		
		
		if((unit = SP.getString("unit", "10")) != currentUnit) //Checking if the unit pref has changed
		{
			Log.w("Unit","Checking pref change");
			currentUnit = unit;
			TextView temperature = (TextView)findViewById(R.id.temperature);
			if(currentUnit.equals("10"))
			{
				Log.w("yeah","bahh");
				currentTemp = SP.getString("temp_C", "");
				temperature.setText(currentTemp);
			}
			else
			{
				currentTemp = SP.getString("temp_F", "");
				temperature.setText(currentTemp);
			}
		}
	}
	
	
	
	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	
	
	public void HeliusClickHandler(View view)
    {
    	switch(view.getId())
    	{
    		case R.id.settings:
    			startActivity(new Intent(this, AppPreferences.class));

    			break;
    	}
    }
	
	
	 private class AsyncWeather extends AsyncTask<String, Integer, String[]>
	    {
	    	ProgressDialog dialog = new ProgressDialog (HeliusMainActivity.this);

			@Override
			protected String[] doInBackground(String... arg0) 
			{
				
				
				NodeList n1,n2;
				String temp1 = arg0[0].replace(" ", "%20");
				
				
			    String URL = "http://free.worldweatheronline.com/feed/weather.ashx?q="+temp1+"&format=xml&num_of_days=3&key=bdb4106ddf154523120210";
				 
				// XML node keys
			     
			     
			     String condition,location,code,time;
			     String arg[] = new String[10];
			  
			      
			     XMLParser parser = new XMLParser();
			     String xml = parser.getXmlFromUrl(URL); // getting XML
			     if(xml == null)								//HOW ARE YOU GONNA HANDLE THIS??????
			     {
			    	 Log.w("lololol", "XML IS NULL BITCH");
			     }
			     Document doc = parser.getDomElement(xml); // getting DOM element
			      
			     
			     n1 = doc.getElementsByTagName("current_condition");
			     n2 = doc.getElementsByTagName("request");
			     
			     Element e = (Element) n1.item(0);
			     Element e1 = (Element) n2.item(0);
			     SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			     
			     
			     condition = parser.getValue(e, "weatherDesc");
			     
			     if(condition == "") //City not found
			     {
			    	 View view = findViewById(R.id.heliusMain);
			    	 view.post(new Runnable() {
			    		  public void run() {
			    			  Toast toast = Toast.makeText(getApplicationContext(), "City Not found. Guess what? You're in Greenwich now!", Toast.LENGTH_LONG);
			 			     toast.show();
			    		  }
			    		});
			    	 
				     Editor edit = SP.edit();
				     edit.putString("location", "Greenwich,uk");
				     edit.commit();
			    	 
			    	 Log.w("bajhahahah", "Conditionis null man");
			    	 URL = "http://free.worldweatheronline.com/feed/weather.ashx?q="+"Greenwich,uk"+"&format=xml&num_of_days=3&key=bdb4106ddf154523120210";
			    	 parser = new XMLParser();
				     xml = parser.getXmlFromUrl(URL);
				     doc = parser.getDomElement(xml);
				     n1 = doc.getElementsByTagName("current_condition");
				     n2 = doc.getElementsByTagName("request");
				     
				     e = (Element) n1.item(0);
				     e1 = (Element) n2.item(0);
				     //SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext()); //WHY?? Exactly!
				    
				     condition = parser.getValue(e, "weatherDesc"); 
			     }
			     
			     tempC = parser.getValue(e, "temp_C")+ " \u00B0"+"C";
			     tempF = parser.getValue(e, "temp_F")+ " \u00B0"+"F";
			     location = parser.getValue(e1, "query");
			     code = parser.getValue(e, "weatherCode");
			     
			     
			     
			     for(int i = 0; i<location.length();i++)
			     {
			    	 if(location.charAt(i) == ',')
			    	 {
			    		 String a = location.substring(0, i);
			    		 location = a;
			    		 break;
			    	 }
			     }
			     
			     
			     arg[0] = "";
			     arg[1] = condition;
			     arg[2] = location;
			     arg[3] = code;
			     arg[4] = tempC;
			     arg[5] = tempF;
			     	
				
				return arg;
			}
			
			@Override
		      protected void onPreExecute() {
		        dialog.setMessage("Hold it...");
		        dialog.setIndeterminate(true);
		        dialog.setCancelable(false);
		        dialog.show();
		     }
			
			protected void onPostExecute(String result[])
			{
				
				/*if(result == null)					//No more nulls baby
				{
					Log.w("llolplplplpllol", "Result null");
					result = new String[10];
					result[4] = SP.getString("temp_C", "");
					result[5] = SP.getString("temp_F", "");
					result[3] = SP.getString("code","");
					result[1] = SP.getString("condition","");
					result[2] = SP.getString("location","");
					 
				}*/
				//else
				{
					Log.w("llolplplplpllol", "Result NOT null");
					Calendar c = Calendar.getInstance();
					SimpleDateFormat df = new SimpleDateFormat("HH:mm   MMM dd, ''yy");
			        String formattedDate = df.format(c.getTime());
			        SimpleDateFormat rev = new SimpleDateFormat("yyMMddHHmm");
			        String orderDate = rev.format(c.getTime());
			        
			        Editor editor = SP.edit();
			        editor.putString("lastUpdate",formattedDate);
			        editor.putString("updateOrder", orderDate);
			        editor.commit();
				}
				int code;
				 code = Integer.parseInt(result[3]);
				
				 ColorTheme theme = new ColorTheme(code, result[1]);
				 
				 
				 View view = findViewById(R.id.heliusMain);
			     view.setBackgroundColor(theme.returnBGColor());
			     
			     if(SP.getString("unit", "10").equals("10") )
						result[0] = result[4];
					else
						result[0] = result[5];
			     
			     Log.w("Chosen",result[0]);
			     Log.w("TEMP_C",result[4]);
			     Log.w("TEMP_F",result[5]);
				
				 TextView tv3 = (TextView)findViewById(R.id.temperature);
				 tv3.setText(result[0]);
				 TextView tv2 = (TextView)findViewById(R.id.condition);
				 tv2.setText(result[1]);
				 TextView tv1 = (TextView)findViewById(R.id.location);
				 tv1.setText(result[2]);
				 
				 dialog.dismiss(); 
				 
				 Editor edit = SP.edit();
				 edit.putString("temp_C", result[4]);
				 edit.putString("temp_F", result[5]);
				 edit.putString("condition", result[1]);
				 edit.putString("location", result[2]);
				 edit.putString("code", result[3]);
				 edit.commit();
				 
			}
	    }
	    
	    /*
	     * 
	     * Dialoggg
	     * 
	     * 
	     */
	    Dialog createCustomDialog()
	    {
	    	
	    	final Dialog dialog = new Dialog(this);

	    	dialog.setContentView(R.layout.dialog_layout);
	    	dialog.setTitle("Your Location");
	    	
	    	Button dialogOK = (Button)dialog.findViewById(R.id.okay);
	    	Button dialogCancel = (Button)dialog.findViewById(R.id.cancel);
	    	
	    	dialogOK.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					EditText location = (EditText) dialog.findViewById(R.id.dialogText);
				
					Editor editor = SP.edit();
					editor.putString("location",location.getText().toString());
					editor.commit();
					
					InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
	    		    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	    		    dialog.hide();   		    
	    		    
	    			new AsyncWeather().execute(location.getText().toString());
	    			currentLocation = location.getText().toString();
					
					dialog.dismiss();
				}
			});
	    	dialogCancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
	    	
	    	return dialog;

	    }
	
    void setFont()
    {
    	TextView temp = (TextView)findViewById(R.id.temperature);
    	TextView cond = (TextView)findViewById(R.id.condition);
    	TextView loc = (TextView)findViewById(R.id.location);
    	
    	Typeface face1=Typeface.createFromAsset(getAssets(),
                "fonts/Asap.ttf");
    	Typeface face2=Typeface.createFromAsset(getAssets(),
                "fonts/Oxygen-Light.ttf");
    	Typeface numface=Typeface.createFromAsset(getAssets(),
                "fonts/Comfortaa-Regular.ttf");
    	
    	temp.setTypeface(numface);
    	cond.setTypeface(face2);
    	loc.setTypeface(face2);
    	
    	cond.setTextColor(0xFF0099cc);
    	loc.setTextColor(0xFF0099cc);
    }   
}
