package in.ganz7.heliusweather.support;

public class ColorTheme 
{
	public int code;
	public String condition;
	public int BGround,TextColor;
	
	
	public ColorTheme(int code, String condition)
	{
		this.code = code;
		this.condition = condition.trim();
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
	
	public void setTheme()
	{
		switch(code){
		
			//snow related stuff
			case 395: case 392: case 377: case 374: case 371: case 368: case 365: case 362: case 350: case 338: case 335: case 332: 
			case 329: case 326: case 323: case 320: case 317: case 314: case 311: case 284: case 281: case 230: case 260: case 227:
			case 179:
				BGround = 0xFF2FA9FA;
			break;
			//heavy rain stuff
			case 359: case 356: case 389: case 308: case 305: case 302: case 299:
				BGround = 0xFF737373;
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
				if(condition.equals("Sunny"))
					BGround = 0xFFE8D900;
				else
					BGround = 0xFFF5F5F5;
			break;	
				
				
		}
	}
	
	
}
