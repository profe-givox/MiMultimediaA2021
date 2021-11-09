package net.ivanvega.mimultimediaa2021;

import androidx.lifecycle.ViewModelProvider;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

public class AudioFragment extends Fragment {

    Button btnRecord, btnPlay;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    private AudioViewModel mViewModel;
    private File audioFile;

    public static AudioFragment newInstance() {
        return new AudioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View layout =
                inflater.inflate(R.layout.audio_fragment, container, false);

        btnRecord = layout.findViewById(R.id.btnGrabar);
        btnPlay = layout.findViewById(R.id.btnReproducir);

        //
        //File rutaExternaCompartida = Environment.getExternalStorageDirectory();


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnPlay.getText().toString().equals("Detener")){
                    mediaPlayer.stop();mediaPlayer.release();
                    btnPlay.setText("Reproducir");
                    return;
                }

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(audioFile.getPath());
                    mediaPlayer.setOnPreparedListener(mediaPlayer1 -> {
                         mediaPlayer1.start();
                         btnPlay.setText("Detener");
                    });
                    mediaPlayer.prepareAsync();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



        btnRecord.setOnClickListener(view -> {
            if(btnRecord.getText().toString().equals("Detener")){
                mediaRecorder.stop();mediaRecorder.release();
                btnRecord.setText("Grabar");
                return;
            }

            audioFile = new File(getActivity().getFilesDir(), "miaudio.mp3");

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mediaRecorder.setOutputFile(audioFile);
            }
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaRecorder.start();   // Recording is now started

            btnRecord.setText("Detener");



            /*
            recorder.stop();
            recorder.reset();   // You can reuse the object by going back to setAudioSource() step
            recorder.release(); // Now the object cannot be reused

             */

        });

        return layout;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AudioViewModel.class);
        // TODO: Use the ViewModel
    }

}