package in.ganz7.heliusweather.support;

import in.ganz7.heliusweather.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

public class ColorTheme 
{
	public int code;
	public String condition;
	public String location;
	public String codePredictions;
	public String localTime;
	public String HHmm;
	public int HHmmInt;
	public int BGround;
	public int TextColor;
	public int wIcon;
	public int iconSet[] = new int[3];
	int white = 0;
	
	
	public ColorTheme(int code, String condition, String location, String codePredictions)
	{
		this.code = code;
		this.condition = condition.trim();
		this.location = location;
		this.codePredictions = codePredictions;
		setTime(); //New Addition
		setTheme();
		
		Log.w("CodePred",codePredictions);
		String[] codePred = codePredictions.split(" ");
		Log.w("CodePred",codePred[1]);
		for(int i=0; i<3; i++)
		{
			iconSet[i] = setIcon(codePred[i]);
		}
	}
	
	public int returnBGColor()
	{
		return BGround;	
	}
	
	public int returnTextColor()
	{
		return TextColor;
	}
	public int returnIcon()
	{
		return wIcon;
	}
	public void setTime() //What if there is no access to the internet? Check that out. Deal with it!
	{
		NodeList localTimeNode;
		location = location.replace(" ", "%20");
		String URLTime = "http://www.worldweatheronline.com/feed/tz.ashx?key=bdb4106ddf154523120210&q="+location+"&format=xml";
		
		XMLParser parser = new XMLParser();
	    String xml = parser.getXmlFromUrl(URLTime);
	    Document doc = parser.getDomElement(xml);
	    localTimeNode = doc.getElementsByTagName("time_zone");
	    Element localTimeElement = (Element) localTimeNode.item(0);
		localTime = parser.getValue(localTimeElement, "localtime");
		Log.w("balls",localTime);
		String[] a = localTime.split(" "); // Splits Date and time
		Log.w("balls", a[1]);
		String[] b = a[1].split(":"); //Splits HH and mm
		HHmm = b[0]+b[1];
		HHmmInt = Integer.parseInt(HHmm);
		
	}
	
	public int setIcon(String strValue)
	{
		int value = Integer.parseInt(strValue);
		if(white == 0)
		{
			switch(value){
			
				//snow related stuff
				case 395: case 392: case 377: case 374: case 371: case 368: case 365: case 362: case 350: case 338: case 335: case 332: 
				case 329: case 326: case 323: case 320: case 317: case 314: case 311: case 284: case 281: case 230: case 260: case 227:
				case 179:
					return R.drawable.snowflake_grey;
				//heavy rain stuff
				case 359: case 356: case 389: case 308: case 305: case 302: case 299:
					return R.drawable.cloud_rain_grey;
				//light rain
				case 353: case 386:	case 296: case 293: case 266: case 263: case 200: case 185: case 182: case 176:
					return R.drawable.cloud_hail_grey;
				//fog
				case 248: case 143:
					return R.drawable.cloud_fog_grey;
				//cloudy
				case 119: case 116: case 122:
					return R.drawable.sun_grey;
				//clear/sunny
				case 113:
					if(condition.equalsIgnoreCase("Sunny"))
					{
						return R.drawable.sun_grey;
					}
					else
					{
						return R.drawable.cloud_sun_grey;
					}		
			}
		}
		else
		{
			switch(value){
			
			//snow related stuff
			case 395: case 392: case 377: case 374: case 371: case 368: case 365: case 362: case 350: case 338: case 335: case 332: 
			case 329: case 326: case 323: case 320: case 317: case 314: case 311: case 284: case 281: case 230: case 260: case 227:
			case 179:
				return R.drawable.cloud_sun;
		
			//heavy rain stuff
			case 359: case 356: case 389: case 308: case 305: case 302: case 299:
				return R.drawable.cloud_sun;

			//light rain
			case 353: case 386:	case 296: case 293: case 266: case 263: case 200: case 185: case 182: case 176:
				return R.drawable.cloud_sun;
			//fog
			case 248: case 143:
				return R.drawable.cloud_sun;
	
			//cloudy
			case 119: case 116: case 122:
				return R.drawable.cloud_sun;
		
			//clear/sunny
			case 113:
				if(condition.equalsIgnoreCase("Sunny"))
				{
					return R.drawable.shades;
				}
				else
				{
					return R.drawable.cloud_sun;
				}		
			}	
		}
		return 0; //Make this better?
	}
	
	public void setTheme()
	{
		switch(code){
		
			//snow related stuff
			case 395: case 392: case 377: case 374: case 371: case 368: case 365: case 362: case 350: case 338: case 335: case 332: 
			case 329: case 326: case 323: case 320: case 317: case 314: case 311: case 284: case 281: case 230: case 260: case 227:
			case 179:
				BGround = 0xFF8D38C9; //Violet
				TextColor = 0xFFF9F9F9;
				wIcon = R.drawable.snowflake_grey;
			break;
			//heavy rain stuff
			case 359: case 356: case 389: case 308: case 305: case 302: case 299:
				BGround = 0xFF707070; //Deep Grey
				TextColor = 0xFFFFFFFF;
				wIcon = R.drawable.cloud_rain_grey;
			break;
			//light rain
			case 353: case 386:	case 296: case 293: case 266: case 263: case 200: case 185: case 182: case 176:
				BGround = 0xFF488AC7; //Steel Blue
				TextColor = 0xFFFFFFFF;
				wIcon = R.drawable.cloud_hail_grey;
			break;	
			//fog
			case 248: case 143:
				BGround = 0xFFE56717; //Orange
				TextColor = 0xFFFFFFFF;
				wIcon = R.drawable.cloud_fog_grey;
			break;	
			//cloudy
			case 119: case 116: case 122:
				BGround = 0xFFF5F5F5;
				TextColor = 0xFF00A6DE;
				wIcon = R.drawable.cloud_grey;
			break;
			//clear/sunny
			case 113:
				if(condition.equalsIgnoreCase("sunny"))
				{
					BGround = 0xFFFFE100;	//Yelloww
					TextColor = 0xFF555555;
					wIcon = R.drawable.sun_grey;
				}
				else
				{
					BGround = 0xFF00A6DE;	//Blueish blue
					TextColor = 0xFFFFFFFF;
					wIcon = R.drawable.cloud_sun_grey;
				}
			break;			
		}
		if((HHmmInt > 1800 && HHmmInt<2000) || (HHmmInt > 0400 && HHmmInt <0700 )) //Twilight 
		{
			BGround = 0xFFDB1A68;//Deep Maroon? idk.
			TextColor = 0xFFFFFFFF;
		}
		if(HHmmInt > 2000 || HHmmInt < 0400) //Night
		{
			BGround = 0xFF000000;
			TextColor = 0xFF00A6DE;
		}
	}
	
	
}
