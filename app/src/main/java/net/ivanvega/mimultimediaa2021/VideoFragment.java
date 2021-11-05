package net.ivanvega.mimultimediaa2021;

import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

import java.io.File;

public class VideoFragment extends Fragment {

    private VideoViewModel mViewModel;
    private Button btnV;
    private VideoView videoV;
    private Uri urivideo;

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout =
        inflater.inflate(R.layout.video_fragment, container, false);

        btnV = (Button)layout.findViewById(R.id.btnVideo);
        videoV = (VideoView)layout.findViewById(R.id.videoView);

        btnV.setOnClickListener(view -> {
            File file =
                    new File(getActivity().getExternalFilesDir(null),
                            "chilakilvideo.mp3");

            Log.d("RUTA", String.valueOf(file.getAbsoluteFile()));

            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);


            urivideo = FileProvider.getUriForFile(getActivity(),
                    "net.ivanvega.mimultimediaa2021" ,
                    file);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, urivideo);

            startActivityForResult(intent, 1001);
        });

        return layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        videoV.setVideoURI(urivideo);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VideoViewModel.class);
        // TODO: Use the ViewModel
    }

}