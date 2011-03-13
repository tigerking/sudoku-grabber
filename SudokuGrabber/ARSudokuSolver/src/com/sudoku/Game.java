package com.sudoku;

import android.app.Activity;
import android.app.Dialog;
import com.sudoku.Keypad;
import android.os.Bundle;
import android.util.Log;

public class Game extends Activity {
	private static final String TAG = "Sudoku";
	public static final String KEY_DIFFICULTY = "com.sudoku.difficulty";
	public static final String KEY_PUZZLE = "com.sudoku.puzzle";
	public static final int DIFFICULTY_EASY= 0;
	public static final int DIFFICULTY_MEDIUM = 1;
	public static final int DIFFICULTY_HARD = 2;
	
	private int puzzle[];
	
	private PuzzleView puzzleView;
	private final boolean valid[][] = new boolean[9][9];
	
	
	private int getTile(int x, int y){
		return puzzle[y*9 + x];
	}
	
	
	protected String getTileString(int x, int y){
		int v = getTile(x,y);
		if(v==0){
			return "";
		}else{
			return String.valueOf(v);
		}
		
	}
	
	protected boolean setTile(int x, int y, int value){
		puzzle[y*9+x] = value;
		validateALlTiles();
		return isValid(x,y);
	}
	protected boolean isValid(int x, int y){
		return valid[x][y];
	}
	
	
	private void validateALlTiles(){
		for(int x = 0; x<9; x++){
			for(int y=0; y<9;y++){
				valid[x][y] = validateTile(x,y);
			}
		}
	}
	
	private boolean validateTile(int x, int y){
		int value = getTile(x,y);
		if(value==0){
			return true;
		}
		for(int i = 0; i<9; i++){
			if(i == y){
				continue;
			}else{
				int t = getTile(x,i);
				if(t ==value){
					return false;
				}
			}
		}
		for(int i = 0; i<9; i++){
			if(i == x){
				continue;
			}else{
				int t = getTile(i,y);
				if(t==value){
					return false;
				}
			}
		}
		
		int startx = (x/3) *3;
		int starty = (y/3) *3;
		for(int i= startx; i<startx+3;i++){
			for(int j= starty; j<starty+3; j++){
				if(i == x && j == y){
					continue;
				}else{
					int t = getTile(i,j);
					if(t== value){
						return false;
					}
				}
			}
			
		}
		return true;
	}
	
	@Override 
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d(TAG,"OnCreate");
		int diff= getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
		puzzle = getIntent().getIntArrayExtra(KEY_PUZZLE);
		validateALlTiles();
		puzzleView = new PuzzleView(this);
		setContentView(puzzleView);
		puzzleView.requestFocus();
	}

	public void showKeypadOrError(int selX, int selY) {
		Dialog v = new Keypad(this,puzzleView);
		v.show();		
	}
}
