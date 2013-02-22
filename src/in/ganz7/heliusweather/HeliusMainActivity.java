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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HeliusMainActivity extends Activity {
	
	private static final boolean AUTO_HIDE = true;
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final boolean TOGGLE_ON_CLICK = true;
	
	SharedPreferences SP;
	String currentLocation,currentUnit="10";
	String currentTemp,tempC, tempF;
	
	Animation a; 
	//a.reset();

	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	
	private SystemUiHider mSystemUiHider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_helius_main);
		
		setFont(); //Sets the custom fonts
		a = AnimationUtils.loadAnimation(this, R.anim.translate);
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
		String temp= SP.getString("location", ""),unit="";
     
        if(currentLocation == "") //Why not use currentLocation instead? YES!
        {
        	createCustomDialog().show();
        }
		
        else if( !temp.equalsIgnoreCase(currentLocation)) //True if the location has been changed.
		{		
			currentLocation = temp;     //--Modifying this in the doInBackground Function
			new AsyncWeather().execute(currentLocation);
		}
		
		else if(temp.equalsIgnoreCase(currentLocation)) //If the location hasn't been changed
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
	        		setCurrentWeather();
	        		setPredictions();
	        }
	        else
	        	new AsyncWeather().execute(currentLocation);
		}
		
		
		if((unit = SP.getString("unit", "10")) != currentUnit) //Checking if the unit preference has changed
		{
			Log.w("Unit","Checking pref change");
			currentUnit = unit;
			setCurrentWeather();
			setPredictions();
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

				NodeList n1,n2,predictionList;
				String temp1 = arg0[0].replace(" ", "%20");
				
			    String URL = "http://free.worldweatheronline.com/feed/weather.ashx?q="+temp1+"&format=xml&num_of_days=4&key=bdb4106ddf154523120210";
				
				// XML node keys
			   
			     String condition,location,code;
			     String arg[] = new String[10];
			  
			     XMLParser parser = new XMLParser();
			     String xml = parser.getXmlFromUrl(URL); // getting XML
			     if(xml == null)								//HOW ARE YOU GONNA HANDLE THIS??????
			     {
			    	 Log.w("lololol", "XML IS NULL BITCH");
			    	 View view = findViewById(R.id.heliusMain);
			    	 view.post(new Runnable() {
			    		  public void run() {
			    			  Toast toast = Toast.makeText(getApplicationContext(), "" +
			    			  		"Oops! There seems to be a problem with the connection.", Toast.LENGTH_LONG); 
			 			     toast.show();
			    		  }
			    		});
			    	 return null;
			     }
			     Document doc = parser.getDomElement(xml); // getting DOM element
			     n1 = doc.getElementsByTagName("current_condition");
			     Element e = (Element) n1.item(0);	//Rename everything appropriately
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
			    	 URL = "http://free.worldweatheronline.com/feed/weather.ashx?q="+"Greenwich,uk"+"&format=xml&num_of_days=4&key=bdb4106ddf154523120210";
			    	 parser = new XMLParser();
				     xml = parser.getXmlFromUrl(URL);
				     doc = parser.getDomElement(xml);
				     n1 = doc.getElementsByTagName("current_condition");
				     
				     e = (Element) n1.item(0);
				    
				     condition = parser.getValue(e, "weatherDesc"); 
			     }
			     n2 = doc.getElementsByTagName("request");
			     predictionList = doc.getElementsByTagName("weather");
			     
			     Element e1 = (Element) n2.item(0);			     
			     
			     tempC = parser.getValue(e, "temp_C")+ " \u00B0"+"C";
			     tempF = parser.getValue(e, "temp_F")+ " \u00B0"+"F";
			     location = parser.getValue(e1, "query");
			     code = parser.getValue(e, "weatherCode");
			     
			     
			     currentLocation = location; //Keeping tabs on the current location
			     
			     String maxPredictionC = "", minPredictionC = "", maxPredictionF="",minPredictionF="",codePrediction = "";
			     
			     for(int i=1; i<4; i++)
			     {
			    	 Element prediction = (Element) predictionList.item(i);
			    	 maxPredictionC = maxPredictionC +parser.getValue(prediction, "tempMaxC")+ " ";
			    	 minPredictionC = minPredictionC +parser.getValue(prediction, "tempMinC")+ " ";
			    	 codePrediction = codePrediction +parser.getValue(prediction, "weatherCode")+ " ";
			    	 maxPredictionF = maxPredictionF +parser.getValue(prediction, "tempMaxF")+ " ";
			    	 minPredictionF = minPredictionF +parser.getValue(prediction, "tempMinF")+ " ";
			    	 
			     }
			     Editor editor = SP.edit();
			     editor.putString("temp_C", tempC);
				 editor.putString("temp_F", tempF);
				 editor.putString("condition", condition);
				 editor.putString("location", location);
				 editor.putString("code", code);
		    	 editor.putString("maxPredictionC",maxPredictionC);
		    	 editor.putString("minPredictionC",minPredictionC);
		    	 editor.putString("maxPredictionF",maxPredictionF);
		    	 editor.putString("minPredictionF",minPredictionF);
		    	 editor.putString("codePredictions",codePrediction);
		    	 editor.commit();
		    	 
		    	 Log.w("Predictions",maxPredictionC+minPredictionC+"1"+codePrediction);
			     
			     //ColorTheme class for theming info.
			     Log.w("baloss",SP.getString("condition", ""));
				 ColorTheme theme = new ColorTheme(Integer.parseInt(code), SP.getString("condition",""),
						 SP.getString("location", ""),SP.getString("codePredictions", ""));
				 
			     
			     Editor edit = SP.edit();
			     edit.putString("bgColor", String.valueOf(theme.returnBGColor()));
			     edit.putString("txtColor", String.valueOf(theme.returnTextColor()));
			     edit.putString("wIcon", String.valueOf(theme.returnIcon()));
		
				 edit.putString("pIcon1", String.valueOf(theme.iconSet[0]));
			     edit.putString("pIcon2", String.valueOf(theme.iconSet[1]));
			     edit.putString("pIcon3", String.valueOf(theme.iconSet[2]));
			     edit.commit();
				//return null;
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
				Calendar c = Calendar.getInstance();
				SimpleDateFormat df = new SimpleDateFormat("HH:mm   MMM dd, ''yy");
		        String formattedDate = df.format(c.getTime());
		        SimpleDateFormat rev = new SimpleDateFormat("yyMMddHHmm");
		        String orderDate = rev.format(c.getTime());
		        
		        Editor editor = SP.edit();
		        editor.putString("lastUpdate",formattedDate);
		        editor.putString("updateOrder", orderDate);
		        editor.commit();
			        
				setCurrentWeather();
				setPredictions();
				 
				dialog.dismiss();  
				 
			}
	    }    
	    /*
	     * Dialoggg 
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
		
					InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
	    		    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	    		    dialog.hide();   		    
	    		    
	    			new AsyncWeather().execute(location.getText().toString());
	    			//currentLocation = location.getText().toString(); //Moving this line to asynctask
					
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
	
	void setCurrentWeather()
	{
		int textColor = Integer.parseInt(SP.getString("txtColor", ""));
		Animation animation = new TranslateAnimation(700,0,0, 0);
		animation.setDuration(1500);
		TextView temperatureTV = (TextView)findViewById(R.id.temperature);
		TextView conditionTV = (TextView)findViewById(R.id.condition);
		TextView locationTV = (TextView)findViewById(R.id.location);
		//Setting the foreGround
		temperatureTV.setTextColor(textColor);
		conditionTV.setTextColor(textColor);
		locationTV.setTextColor(textColor);
		
		//Setting the background
	    View view = findViewById(R.id.heliusMain);
	    view.setBackgroundColor(Integer.parseInt(SP.getString("bgColor", "")));
		
		//Setting the temperature, location and condition
		conditionTV.setText(SP.getString("condition",""));
		conditionTV.startAnimation(animation);
		
		if(SP.getString("unit", "10").equals("10") )
			temperatureTV.setText(SP.getString("temp_C", ""));
		else
			temperatureTV.setText(SP.getString("temp_F", ""));
		temperatureTV.startAnimation(animation);
		String location = SP.getString("location","");
		for(int i = 0; i<location.length();i++)
	     {
	    	 if(location.charAt(i) == ',')
	    	 {
	    		 String temporary = location.substring(0, i);
	    		 location = temporary;
	    		 break;
	    	 }
	     }
		locationTV.setText(location);
		locationTV.startAnimation(animation);
		
		ImageView image = (ImageView) findViewById(R.id.weatherCon);
		image.setImageResource(Integer.parseInt(SP.getString("wIcon", "")));
		
	}
	    
	void setPredictions()
	{
		int textColor = Integer.parseInt(SP.getString("txtColor", ""));
		String week[] = {"MON","TUE","WED","THU","FRI","SAT","SUN"};
		String maxPrediction,minPrediction;
		TextView max1 = (TextView)findViewById(R.id.max1);
		TextView max2 = (TextView)findViewById(R.id.max2);
		TextView max3 = (TextView)findViewById(R.id.max3);
		TextView min1 = (TextView)findViewById(R.id.min1);
		TextView min2 = (TextView)findViewById(R.id.min2);
		TextView min3 = (TextView)findViewById(R.id.min3);
		TextView day1 = (TextView)findViewById(R.id.day1);
		TextView day2 = (TextView)findViewById(R.id.day2);
		TextView day3 = (TextView)findViewById(R.id.day3);
		
		max1.setTextColor(textColor);max2.setTextColor(textColor);max3.setTextColor(textColor);
		min1.setTextColor(textColor);min2.setTextColor(textColor);min3.setTextColor(textColor);
		day1.setTextColor(textColor);day2.setTextColor(textColor);day3.setTextColor(textColor);
		
		if(SP.getString("unit", "10").equals("10"))
		{
			maxPrediction = SP.getString("maxPredictionC", "");
			minPrediction = SP.getString("minPredictionC", "");
		}
		else
		{
			maxPrediction = SP.getString("maxPredictionF", "");
			minPrediction = SP.getString("minPredictionF", "");
		}

		
		String max[] = maxPrediction.split(" ");
		String min[] = minPrediction.split(" ");
		/*
		 * Setting the Max and Min textviews 
		 */
		max1.setText(max[0]);max2.setText(max[1]);max3.setText(max[2]);
		min1.setText(min[0]);min2.setText(min[1]);min3.setText(min[2]);
		/*
		 * Setting the days
		 */
		int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		Log.w("day",String.valueOf(day));
		if(day == 7) day = 0;
		day1.setText(week[day++]); if(day == 7) day = 0;
		day2.setText(week[day++]); if(day == 7) day = 0;
		day3.setText(week[day++]);
		
		//Setting the weather icons
		ImageView pIcon1 = (ImageView) findViewById(R.id.pIcon1);
		ImageView pIcon2 = (ImageView) findViewById(R.id.pIcon2);
		ImageView pIcon3 = (ImageView) findViewById(R.id.pIcon3);
		pIcon1.setImageResource(Integer.parseInt(SP.getString("pIcon1", "")));
		pIcon2.setImageResource(Integer.parseInt(SP.getString("pIcon2", "")));
		pIcon3.setImageResource(Integer.parseInt(SP.getString("pIcon3", "")));
		//Firing up the animations
		max1.startAnimation(a);
		min1.startAnimation(a);
		day1.startAnimation(a);
		try{Thread.sleep(500);}catch(Exception e){e.printStackTrace();}
		max2.startAnimation(a);
		min2.startAnimation(a);
		day2.startAnimation(a);
		try{Thread.sleep(500);}catch(Exception e){e.printStackTrace();}
		max3.startAnimation(a);
		min3.startAnimation(a);
		day3.startAnimation(a);
		
		mSystemUiHider.hide();
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
    }   
}
