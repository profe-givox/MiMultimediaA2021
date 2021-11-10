package net.ivanvega.mimultimediaa2021;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class AudioFragment extends Fragment {

    Button btnRecord, btnPlay;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    private AudioViewModel mViewModel;
    private File audioFile;
    private ActivityResultLauncher<String[]> launcherPermis;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Boolean permissionToWriteAccepted= false;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        launcherPermis = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            result.forEach((s, aBoolean) -> {
                                Log.i("PERMISOS CALLBACK", s + String.valueOf(aBoolean));
                            });
                        }
                        permissionToRecordAccepted = result.get(permissions[0]);
                        permissionToWriteAccepted = result.get(permissions[1]);
                        btnRecord.setEnabled(permissionToRecordAccepted);
                        btnPlay.setEnabled(permissionToWriteAccepted);
                    }
                });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

            //audioFile = new File(getActivity().getFilesDir(), "miaudio.mp3");
            audioFile = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                    , "miaudiochilakil.mp3");

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


        launcherPermis.launch(permissions);

        return layout;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AudioViewModel.class);
        // TODO: Use the ViewModel
    }

}