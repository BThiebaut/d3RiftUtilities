package benjamin.thiebaut.fr.d3RiftUtilities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    Button button;
    ImageView superior;
    ImageView simple;
    TextView textSup;
    TextView textSimple;
    EditText gain;
    List<Integer> lastJets = new ArrayList<Integer>();
    private ProgRunnable mRunnable;

    private static class MyHandler extends Handler {}
    private final MyHandler mHandler = new MyHandler();

    public static class ProgRunnable implements Runnable {
        private final WeakReference<Activity> mActivity;
        private ImageView sup;
        private ImageView simple;
        List<Integer> lastJets;
        TextView textSup;
        TextView textSimple;
        EditText gain;

        int defaultGain = 5;

        public ProgRunnable(Activity activity, ImageView sup, ImageView simple, List liste, TextView textSup, TextView textSimple, EditText gain) {
            this.sup = sup;
            this.simple = simple;
            this.lastJets = liste;
            this.textSimple = textSimple;
            this.textSup = textSup;
            this.gain = gain;
            mActivity = new WeakReference<>(activity);
        }

        private Integer getNbSup(){
            int counter = 0;
            for ( int i : this.lastJets){
                if (i == 1){
                    counter++;
                }
            }
            return counter;
        }

        private Integer getNbSimple(){
            int counter = 0;
            for ( int i : this.lastJets){
                if (i == 0){
                    counter++;
                }
            }
            return counter;
        }

        private int getPercentSimple(int gain){
            int nbSimple = getNbSimple();
            int nbSup = getNbSup();
            int percSimple = 50;

            if (nbSup > nbSimple){
                // Augmente les chances de simples
                percSimple += nbSup * gain;
            }else if (nbSimple > nbSup){
                // Diminue les chances de simple
                percSimple -= nbSimple * gain;
            }

            return percSimple;
        }

        private int getPercentSup(int gain){
            int nbSimple = getNbSimple();
            int nbSup = getNbSup();
            int percSup = 50;

            if (nbSup > nbSimple){
                // Diminue les chances de sup
                percSup -= nbSup * gain;
            }else if (nbSimple > nbSup){
                // Augmente les chances de sup
                percSup += nbSimple * gain;
            }

            return percSup;
        }

        @Override
        public void run() {
            Activity activity = mActivity.get();
            if (activity != null) {
                Random r = new Random();
                int randResult = 0 + r.nextInt(100);
                int percSimple = 50;
                int percSup = 50;
                int gain = defaultGain;

                try {
                    gain = Integer.parseInt(this.gain.getText().toString());
                }catch (Exception e){
                    // on reset en cas d'exeption
                    this.gain.setText("5");
                }

                // Vérifie les 5 derniers jets

                if (this.lastJets.size() > 0){
                    int nbSup = getNbSup();
                    int nbSimple = getNbSimple();

                    if (nbSup > nbSimple){
                        // Augmente les chances de simples
                        randResult += nbSup * gain;

                        if (randResult > 50){
                            // réinitialise les chances
                            this.lastJets.clear();
                        }

                    }else if (nbSimple > nbSup){
                        // Aumente les chances de sup
                        randResult -= nbSimple * gain;

                        if (randResult < 51){
                            // réinitialise les chances
                            this.lastJets.clear();
                        }

                    } // Sinon ne fait rien

                }

                if (randResult < 51){
                    // Faille SUP
                    simple.setVisibility(View.INVISIBLE);
                    sup.setVisibility(View.VISIBLE);
                    this.lastJets.add(1);
                }else {
                    // Faille SIMPLE
                    simple.setVisibility(View.VISIBLE);
                    sup.setVisibility(View.INVISIBLE);
                    this.lastJets.add(0);
                }

                // Affiche les pourcentage de chance du prochain jet
                this.textSup.setText("GR :\n" + getPercentSup(gain) + " %");
                this.textSimple.setText("R :\n" + getPercentSimple(gain) + " %");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        addListenerOnButton();
    }


    public void addListenerOnButton() {

        button = (Button) findViewById(R.id.button1);
        superior = (ImageView) findViewById(R.id.failleSup);
        simple = (ImageView) findViewById(R.id.failleSimple);
        textSimple = (TextView) findViewById(R.id.textSimple);
        textSup = (TextView) findViewById(R.id.textSup);
        gain = (EditText) findViewById(R.id.gain);

        mRunnable = new ProgRunnable(this, superior, simple, lastJets, textSup, textSimple, gain);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                simple.setVisibility(View.INVISIBLE);
                superior.setVisibility(View.INVISIBLE);
                mHandler.postDelayed(mRunnable, 200);
            }

        });

    }

}
