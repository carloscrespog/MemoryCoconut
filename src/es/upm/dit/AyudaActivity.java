package es.upm.dit;

import android.app.Activity;
import android.os.Bundle;

public class AyudaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ayuda_layout); //Cargamos la vista
		setTitle(R.string.title_ayuda);
	}
	

}
