package com.sudoku;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteOrder;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


public class SudokuGrabber extends Activity {
	private static final String IMAGE_DIRECTORY = "/sdcard/DCIM/Camera";
	private static final int ACTIVITY_SELECT_CAMERA = 0;
	private static final int ACTIVITY_SELECT_IMAGE = 1;
	private static final String TAG = "MAIN_ACTIVITY";
	private static final int CAMERA_ID = Menu.FIRST;
	private static final int GALLERY_ID = Menu.FIRST + 1;
	private String mCurrentImagePath = null;
	private SudokuDetector sd = new SudokuDetector();
	private TextView tv;
	private int[][] sudoku = new int[9][9];
	private int byteOrder = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tv = new TextView(this);
		setContentView(tv);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, CAMERA_ID, 0, "Camera");
		menu.add(0, GALLERY_ID, 0, "Gallery");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case CAMERA_ID:
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			long timeTaken = System.currentTimeMillis();
			mCurrentImagePath = IMAGE_DIRECTORY + "/"
					+ Utility.createName(timeTaken) + ".jpg";
			Log.i(TAG, mCurrentImagePath);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(mCurrentImagePath)));
			startActivityForResult(cameraIntent, ACTIVITY_SELECT_CAMERA);
			return true;
		case GALLERY_ID:
			Intent galleryIntent = new Intent(Intent.ACTION_PICK,
					Images.Media.INTERNAL_CONTENT_URI);
			startActivityForResult(galleryIntent, ACTIVITY_SELECT_IMAGE);
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTIVITY_SELECT_CAMERA
				&& resultCode == Activity.RESULT_OK) {
			ContentValues values = new ContentValues();
			int degrees = Utility.getRotationFromImage(mCurrentImagePath);
			try {
				ExifInterface exif = new ExifInterface(mCurrentImagePath);
				float[] position = new float[2];
				if (exif.getLatLong(position)) {
					values.put(Images.Media.LATITUDE, position[0]);
					values.put(Images.Media.LONGITUDE, position[1]);
				}
			} catch (Exception e) {

			}
			// reduce the size of image
			try {
				BitmapFactory.Options option = new BitmapFactory.Options();
				option.inSampleSize = 4;
				Bitmap bitmap = BitmapFactory.decodeFile(mCurrentImagePath,
						option);
				if (degrees != 0) {
					bitmap = Utility.rotate(bitmap, degrees);
				}
				FileOutputStream out = new FileOutputStream(mCurrentImagePath);
				bitmap.compress(CompressFormat.JPEG, 100, out);
			} catch (Exception e) {

			}
			values.put(Images.Media.MIME_TYPE, "image/jpeg");
			values.put(Images.Media.DATA, mCurrentImagePath);
			values.put(Images.Media.ORIENTATION, degrees);
			getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
		}
		if (requestCode == ACTIVITY_SELECT_IMAGE && resultCode == RESULT_OK) {
			try {
				Uri currImageURI = data.getData();
				String[] proj = { Images.Media.DATA, Images.Media.ORIENTATION };
				Cursor cursor = managedQuery(currImageURI, proj, null, null,
						null);
				int columnIndex = cursor.getColumnIndex(proj[0]);
				cursor.moveToFirst();
				mCurrentImagePath = cursor.getString(columnIndex);
				Bitmap bitmap = BitmapFactory.decodeFile(mCurrentImagePath);
				Log.i(TAG, mCurrentImagePath);
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				int[] pixels = new int[width * height];
				bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
				sd.setSourceImage(pixels, width, height);
				getByteOrder();
				if(byteOrder != -1){
					sudoku = sd.detectSudoku(byteOrder);
					Intent i = new Intent(this,Game.class);
					i.putExtra(Game.KEY_PUZZLE, setPuzzle(sudoku));
					startActivity(i);
		
				}
							
			} catch (Exception e) {
			}
		}
	}
	private void getByteOrder(){
		String order = ByteOrder.nativeOrder().toString();
		if(order.equals(ByteOrder.BIG_ENDIAN.toString())){
			byteOrder = SudokuDetector.BIG_ENDIAN;
		}else if(order.equals(ByteOrder.LITTLE_ENDIAN.toString())){
			byteOrder = SudokuDetector.LITTLE_ENDIAN;
		}else{
			byteOrder = -1;
		}
	}
	
	public int[] setPuzzle(int sudoku[][]){
		int puzzle[] = new int[81];
		for(int i=0;i<9;i++){
			for(int j=0; j<9; j++){
				puzzle[i*9+j] = sudoku[i][j];
			}
		}
		return puzzle;
	}
}