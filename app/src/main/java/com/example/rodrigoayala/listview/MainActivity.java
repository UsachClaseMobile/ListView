package com.example.rodrigoayala.listview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    ListView mListView;
    MyAdapter mAdapter;
    EditText mEmailEdText;
    Button mAgregarButton;
    ArrayList<String> emails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        CharSequence[] arreglo = {"opcion1", "opcion2"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elige opcion")
                .setSingleChoiceItems(arreglo, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int elemento) {


                    }
                })
                .create().show();

        mListView = (ListView) findViewById(R.id.list_view);
        mEmailEdText = (EditText) findViewById(R.id.edit_text_mail);
        mAgregarButton = (Button) findViewById(R.id.agregar_button);

        if (savedInstanceState == null) {
            emails = new ArrayList<>();
            emails.add("leo.soto@continuum.cl");
            emails.add("rodrigo.ayala@continuum.cl");
        } else {
            emails = savedInstanceState.getStringArrayList("emails");
        }

        mAdapter = new MyAdapter(this,R.layout.custom_row, emails);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedEmail = mAdapter.getItem(position);

                Toast.makeText(MainActivity.this, selectedEmail,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        mAgregarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable mEditable = mEmailEdText.getText();
                if (mEditable.toString().isEmpty()){
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Error")
                            .setMessage("Debe ingresar un email")
                            .setPositiveButton("Ok", null)
                            .show();

                } else {
                    // Esconde el teclado
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEmailEdText.getWindowToken(), 0);

                    Handler mHandler = new Handler();
                    // Muestra un dialogo de progreso
                    final ProgressDialog mDialog =
                            ProgressDialog.show(MainActivity.this, "Espere un momento...",
                                    "Registrando el nuevo usuario", true, false);

                    mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            // EL usuario cerró el dialog
                        }
                    });

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.add(mEmailEdText.getText().toString());
                            mAdapter.notifyDataSetChanged();
                            mEmailEdText.getText().clear();
                            mDialog.dismiss();

                            Toast.makeText(MainActivity.this, "Ha sido agregado un nuevo elemento",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }, 3000);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("emails", emails);
    }

    private class MyAdapter extends ArrayAdapter<String> {

        public MyAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }

        public View getView(int position, @Nullable View fila, @NonNull ViewGroup parent) {

            LayoutInflater mInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (fila == null){
                // Instanciar la fila
                fila = mInflater.inflate(R.layout.custom_row, null);
            }

            ImageView perfilImageView = (ImageView) fila.findViewById(R.id.perfilImageView);
            TextView mailImageView = (TextView) fila.findViewById(R.id.mailTextView);

            String correo = getItem(position);
            mailImageView.setText(correo);

            String hash = md5(correo);

            Log.d("MD5",hash);

            Picasso.with(getContext()).load("https://www.gravatar.com/avatar/"+hash+".png").into(perfilImageView);

            return fila;
        }
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
