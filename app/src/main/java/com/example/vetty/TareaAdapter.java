package com.example.vetty;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetty.controllers.TareaController;
import com.example.vetty.models.tarea;

import java.util.ArrayList;
import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.ViewHolder> {

    private final List<tarea> lista = new ArrayList<>();
    private final TareaController ctrl;

    public TareaAdapter(TareaController ctrl) { this.ctrl = ctrl; }

    public void setData(List<tarea> nuevas) {
        lista.clear(); lista.addAll(nuevas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View vItem = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_tarea, p, false);
        return new ViewHolder(vItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int i) { h.bind(lista.get(i)); }
    @Override public int getItemCount() { return lista.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox chk; TextView txt; ImageButton btnDel;
        ViewHolder(View v) { super(v);
            chk = v.findViewById(R.id.checkCompletada);
            txt = v.findViewById(R.id.txtDescripcion);
            btnDel = v.findViewById(R.id.btnEliminar);
        }
        void bind(tarea t){
            txt.setText(t.descripcion);
            chk.setChecked(t.completada);

            chk.setOnCheckedChangeListener((b,c)->{
                t.completada = c;
                ctrl.actualizarTarea(t)
                        .andThen(ctrl.sincronizarTareasConFirestore())
                        .subscribe(()->{},Throwable::printStackTrace);
            });

            btnDel.setOnClickListener(v->{
                ctrl.eliminarTarea(t)
                        .andThen(ctrl.sincronizarTareasConFirestore())
                        .subscribe(()->{},Throwable::printStackTrace);
            });
        }
    }
}