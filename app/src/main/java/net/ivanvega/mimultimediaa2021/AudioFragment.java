package net.ivanvega.mimultimediaa2021;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AudioFragment extends Fragment {

    Button btnRecord, btnPlay;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    private AudioViewModel mViewModel;
    private File audioFile;
    private ActivityResultLauncher<String[]> launcherPermis;
    private ActivityResultLauncher<String[]> launcherSAF ;


    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE };
    private Boolean permissionToWriteAccepted= false;
    private View btnMulLis;


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

        launcherSAF = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                result -> {
                    Log.d("TESTOpenD", result.toString());
                }

        );
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
        Button btnSAF = layout.findViewById(R.id.btnSAF);

        btnSAF.setOnClickListener(view -> {
            launcherSAF.launch(new String[]{"application/pdf",
                    "application/msword",
                    "application/ms-doc",
                    "application/doc",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "text/plain"});
        });

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

        btnMulLis = layout.findViewById(R.id.btnMulti);
        btnMulLis.setOnClickListener(view -> listarMultimedia() );

        launcherPermis.launch(permissions);

        return layout;


    }

    List<Video> videoList = new ArrayList<Video>();

    private void listarMultimedia() {

        String[] projection = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
        };
        String selection = MediaStore.Video.Media.DURATION +
                " >= ?";
        String[] selectionArgs = new String[] {
                String.valueOf(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))
        };

        String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";

        try (Cursor cursor = getActivity().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                videoList.add(new Video(contentUri, name, duration, size));
            }
            Toast.makeText(getActivity(), ""+ videoList.size(),
                    Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                videoList.forEach(video -> {
                    Log.i("Video1Minuto", video.toString());
                });
            }
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AudioViewModel.class);
        // TODO: Use the ViewModel
    }

}