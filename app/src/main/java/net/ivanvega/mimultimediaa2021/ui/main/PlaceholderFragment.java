package net.ivanvega.mimultimediaa2021.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import net.ivanvega.mimultimediaa2021.R;
import net.ivanvega.mimultimediaa2021.databinding.FragmentMainBinding;

import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private FragmentMainBinding binding;
     ImageView imageViewFoto;
    private Uri uriPtho;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.sectionLabel;
        imageViewFoto =  binding.img;

        final Button btnFoto = binding.btnFoto;
        btnFoto.setOnClickListener(view -> {
            File file =
                    new File(getActivity().getExternalFilesDir(null),
                            "chilakil.jpg");

            Log.d("RUTA", String.valueOf(file.getAbsoluteFile()));

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


             uriPtho = FileProvider.getUriForFile(getActivity(),
                    "net.ivanvega.mimultimediaa2021" ,
                    file);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPtho);

            startActivityForResult(intent, 1001);

        });
        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageViewFoto.setImageURI(uriPtho);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}