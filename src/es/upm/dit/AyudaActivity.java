package es.upm.dit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AyudaActivity extends Activity implements View.OnClickListener {
	private Button cocoBtn;
	private Vibrator vibrator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ayuda_layout); //Cargamos la vista
		setTitle(R.string.title_ayuda);
		cocoBtn = (Button)findViewById(R.id.cocoBtn);
		cocoBtn.setOnClickListener(this); //this referencia a esta clase, necesita implementar la interfaz View.OnClickListener para k funcione (le obliga a tner metodo onClick)
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
	}
	public void onClick(View view) {
		vibrator.vibrate(500); //activamos vibracion
		ImageView imageView = new ImageView(this); //sacamos la imagen por pantalla
		imageView.setImageResource(R.drawable.cocomalo);
		setContentView(imageView);
	}
	

}
