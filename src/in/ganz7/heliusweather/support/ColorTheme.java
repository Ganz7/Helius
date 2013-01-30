package in.ganz7.heliusweather.support;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

public class ColorTheme 
{
	public int code;
	public String condition;
	public String location;
	public String localTime;
	public String HHmm;
	public int HHmmInt;
	public int BGround,TextColor;
	
	
	public ColorTheme(int code, String condition, String location)
	{
		this.code = code;
		this.condition = condition.trim();
		this.location = location;
		setTime(); //New Addition
		setTheme();
	}
	
	public int returnBGColor()
	{
		return BGround;	
	}
	
	public int returnTextColor()
	{
		return TextColor;
	}
	public void setTime() //What if there is no access to the internet? Check that out. Deal with it/
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
		String a[] = localTime.split(" "); // Splits Date and time
		Log.w("balls", a[1]);
		String b[] = a[1].split(":"); //Splits HH and mm
		HHmm = b[0]+b[1];
		//HHmm = "2359";
		HHmmInt = Integer.parseInt(HHmm);
		
	}
	
	public void setTheme()
	{
		switch(code){
		
			//snow related stuff
			case 395: case 392: case 377: case 374: case 371: case 368: case 365: case 362: case 350: case 338: case 335: case 332: 
			case 329: case 326: case 323: case 320: case 317: case 314: case 311: case 284: case 281: case 230: case 260: case 227:
			case 179:
				BGround = 0xFF8D38C9; //Violet
			break;
			//heavy rain stuff
			case 359: case 356: case 389: case 308: case 305: case 302: case 299:
				BGround = 0xFF707070; //Deep Grey
			break;
			//light rain
			case 353: case 386:	case 296: case 293: case 266: case 263: case 200: case 185: case 182: case 176:
				BGround = 0xFFE3E3E3; 
			break;	
			//fog
			case 248: case 143:
				BGround = 0xFFC9C9C9;
			break;	
			//cloudy
			case 119: case 116: case 122:
				BGround = 0xFFF5F5F5;
			break;
			//clear/sunny
			case 113:
				if(condition.equalsIgnoreCase("Sunny"))
					BGround = 0xFFFFE100;
				else
					BGround = 0xFF00A6DE; //Blueish blue
			break;			
		}
		if((HHmmInt > 1800 && HHmmInt<2000) || (HHmmInt > 0400 && HHmmInt <0700 )) //Twilight 
		{
			BGround = 0xFFDB1A68;	//Deep Maroon? idk.
		}
		if(HHmmInt > 2000 || HHmmInt < 0400) //Night
		{
			BGround = 0xFF000000;
		}
	}
	
	
}
