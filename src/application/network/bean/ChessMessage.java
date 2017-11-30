package application.network.bean;

import java.io.Serializable;

public class ChessMessage implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public int a_1,a_2,b_1,b_2;
	public String message;
	public MessageType type;
	public enum MessageType{system,motion,chat}
	
	public ChessMessage(int a1,int b1,int a2, int b2){
		a_1=a1;
		a_2=a2;
		b_1=b1;
		b_2=b2;
		type=MessageType.motion;
	}
	public ChessMessage(String s){
		message=s;
		type=MessageType.chat;
	}
	public String toString(){
		if(type==MessageType.motion){
			return String.format("Opponent Movement: [%d][%d] --> [%d][%d]", a_1,b_1,a_2,b_2);//"Opponent Movement: ["+a_1+"]["+b_1+"] --> ["+a_2+"]["+b_2+"]";
		}
		else if(type==MessageType.chat){
			return "Message:"+message;
		}
		else{
			return null;
		}
	}
}

