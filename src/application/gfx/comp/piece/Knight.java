package application.gfx.comp.piece;

import java.util.ArrayList;

import application.ChessApplication;
import application.gfx.comp.Drawable;

public class Knight extends Piece{

	public Knight(ChessApplication abax, PieceColor col) {
		super(abax, col, Piece.PIECE_KNIGHT);
	}

	@Override
	public ArrayList<Drawable> getValidMoveLocations() {
		ArrayList<Drawable> motion = new ArrayList<Drawable>();
		int[] x={-1,-1,1,1,2,2,-2,-2};
		int[] y={2,-2,-2,2,-1,1,-1,1};
		for(int i=0;i<x.length;i++){
			testMovement(x[i], y[i], this, motion);
		}
		return motion;
	}
	
}
