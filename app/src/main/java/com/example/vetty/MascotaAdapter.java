package com.example.vetty;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class MascotaAdapter extends RecyclerView.Adapter<MascotaAdapter.ViewHolder> {

    private List<Map<String, Object>> mascotaList;
    private OnMascotaClickListener listener;


    public interface OnMascotaClickListener {
        void onEditar(int position);
        void onEliminar(int position);
    }

    public MascotaAdapter(List<Map<String, Object>> mascotaList, OnMascotaClickListener listener) {
        this.mascotaList = mascotaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mascota, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> mascota = mascotaList.get(position);

        holder.nombre.setText("Nombre: " + (String) mascota.get("nombre"));
        holder.especie.setText("Especie: " + (String) mascota.get("especie"));
        holder.edad.setText("Edad: " + (String) mascota.get("edad"));
        holder.raza.setText("Raza: " + (String) mascota.get("raza"));
        holder.sexo.setText("Sexo: " + (String) mascota.get("sexo"));


        holder.btnEditar.setOnClickListener(v -> listener.onEditar(position));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(position));
    }

    @Override
    public int getItemCount() {
        return mascotaList.size();
    }


    public void actualizarLista(List<Map<String, Object>> nuevaLista) {
        mascotaList.clear();
        mascotaList.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, especie, edad, raza, sexo;
        Button btnEditar, btnEliminar;

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreMascotaTextView);
            especie = itemView.findViewById(R.id.especieMascotaTextView);
            edad = itemView.findViewById(R.id.edadMascotaTextView);
            raza = itemView.findViewById(R.id.razaMascotaTextView);
            sexo = itemView.findViewById(R.id.sexoMascotaTextView);
            btnEditar = itemView.findViewById(R.id.btnEditarMascota);
            btnEliminar = itemView.findViewById(R.id.btnEliminarMascota);
        }
    }
}
