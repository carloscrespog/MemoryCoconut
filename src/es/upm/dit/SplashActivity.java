package es.upm.dit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		Thread splashThread = new Thread() {
			   @Override
			   public void run() {
				   try {
					   int waited = 0;
					   while (waited < 2000) {
						   sleep(100);
						   waited += 100;
					   }
				   } catch (InterruptedException e) {
					   e.printStackTrace();
				   } finally {
					   finish();
					   startActivity(new Intent(getApplicationContext(), MemoryCoconutActivity.class));
				   }
			   }
		   };
		   splashThread.start();

	}

}
