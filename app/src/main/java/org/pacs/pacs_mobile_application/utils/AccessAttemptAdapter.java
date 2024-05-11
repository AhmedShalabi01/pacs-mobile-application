package org.pacs.pacs_mobile_application.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.pacs.pacs_mobile_application.R;
import org.pacs.pacs_mobile_application.pojo.responsemodel.AccessAttemptModel;

import java.util.List;

public class AccessAttemptAdapter extends RecyclerView.Adapter<AccessAttemptAdapter.ViewHolder> {

    private List<AccessAttemptModel> accessAttempts;

    public AccessAttemptAdapter(List<AccessAttemptModel> accessAttempts) {
        this.accessAttempts = accessAttempts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_access_attempt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccessAttemptModel accessAttempt = accessAttempts.get(position);
        holder.textViewLocation.setText(accessAttempt.getLocation());
        holder.textViewTime.setText(accessAttempt.getTimeAccess());
        holder.textViewState.setText(accessAttempt.getDecision());
    }

    @Override
    public int getItemCount() {
        return accessAttempts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewLocation;
        TextView textViewTime;
        TextView textViewState;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewState = itemView.findViewById(R.id.textViewState);
        }
    }
}
