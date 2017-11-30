package application.gfx.comp.piece;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import application.ChessApplication;
import application.gfx.comp.piece.Piece.PieceColor;

public class PieceFactory{
	
	private static Map<String,Class<?>> map;
	
	public static Class<?> get(String clas){
		if(map==null){
			map = new HashMap<String,Class<?>>();
			map.put("pawn" , Pawn.class);
			map.put("queen", Queen.class);
			map.put("king" , King.class);
			map.put("tower", Tower.class);
			map.put("bishop", Bishop.class);
			map.put("knight", Knight.class);
		}
		return map.get(clas);
	}
	public static Piece getPiece(String clas,PieceColor col,ChessApplication app){
		Class<?> c = get(clas);
		try{
			Constructor<?>[] cc = c.getConstructors();
			Piece pic = (Piece)cc[0].newInstance(app,col);
			return pic;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}