package com.sudoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class OpenMenu extends Activity implements OnClickListener {
	
	private static final String TAG ="Sudoku Grabber";
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.open_menu);
		
		View grabSudokuButton = findViewById(R.id.grab_sudoku_button);
		grabSudokuButton.setOnClickListener(this);
		
		View playSudokuButton = findViewById(R.id.play_sudoku_button);
		playSudokuButton.setOnClickListener(this);
		
		View aboutButton = findViewById(R.id.about_button);
		aboutButton.setOnClickListener(this);
		
		View exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.about_button:
			Intent aboutAct = new Intent(this,AboutInfo.class);
			startActivity(aboutAct);
			break;
		case R.id.grab_sudoku_button:
			Intent grabSudokuAct = new Intent(this, SudokuGrabber.class);
			startActivity(grabSudokuAct);
			break;
		case R.id.play_sudoku_button:
			startGame();
			break;
		case R.id.exit_button:
			this.finish();
			break;
		default:
			break;
		}
		
	}
	
	private void startGame(){
		Log.d(TAG,"Start New Game ");
	}
}
