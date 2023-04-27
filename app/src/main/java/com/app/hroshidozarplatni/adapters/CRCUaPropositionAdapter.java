package com.app.hroshidozarplatni.adapters;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.hroshidozarplatni.R;
import com.app.hroshidozarplatni.models.AdapterModel;
import com.app.hroshidozarplatni.models.CRCUaProposition;
import com.app.hroshidozarplatni.models.TitleModel;
import com.app.hroshidozarplatni.utils.SizeAwareTextView;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class CRCUaPropositionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AdapterModel> data;
    private ICallback callback;

    public CRCUaPropositionAdapter(List<AdapterModel> data, ICallback callback) {
        this.data = data;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == AdapterModel.Type.TYPE_FAV_ITEM.ordinal()) {
            View textView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.proposition_item_special, parent, false);
            return new PropositionHolder(textView);
        } else if (viewType == AdapterModel.Type.TYPE_DEF_ITEM.ordinal()) {
            View textView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.proposition_item_default, parent, false);
            return new PropositionHolder(textView);
        } else if (viewType == AdapterModel.Type.TYPE_HEADER.ordinal()) {
            View textView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.proposition_header, parent, false);
            return new HeaderHolder(textView);
        } else {
            View textView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.proposition_footer, parent, false);
            return new FooterHolder(textView);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PropositionHolder) {
            PropositionHolder propositionHolder = (PropositionHolder) holder;
            CRCUaProposition pr = (CRCUaProposition) getItem(position);

            View.OnClickListener clickListener = view -> callback.onClick(pr);

            String returnDays = pr.getDays();

            if (!TextUtils.isEmpty(pr.getUrl())) {
                if (propositionHolder.itemRoot != null) {
                    propositionHolder.itemRoot.setOnClickListener(clickListener);
                }
                if (propositionHolder.btnApply != null) {
                    propositionHolder.btnApply.setOnClickListener(clickListener);
                }
                if (propositionHolder.t3 != null) {
                    propositionHolder.t3.setText(R.string.rates_from);
                }
            } else {
                returnDays = "62";

                if (propositionHolder.t3 != null) {
                    propositionHolder.t3.setText(R.string.rates);
                }
            }

            Picasso.get().load(pr.getImage()).into(propositionHolder.ivLogo);



            int daysStr = R.string.day5_more;

            if (!TextUtils.isEmpty(returnDays)) {
                try {
                    int d = Integer.valueOf(returnDays) % 10;
                    if (d >= 5) {
                        daysStr = R.string.day5_more;
                    } else if (d >= 2) {
                        daysStr = R.string.day2_4;
                    } else if (d == 1) {
                        daysStr = R.string.day1;
                    }
                } catch (Exception e) {
                }
            }
            if (pr.getType() == AdapterModel.Type.TYPE_DEF_ITEM) {
                //hack1
                {
                    final List<SizeAwareTextView> textViewList = new ArrayList<>();
                    textViewList.add((SizeAwareTextView) propositionHolder.t1);
                    textViewList.add((SizeAwareTextView) propositionHolder.t2);
                    textViewList.add((SizeAwareTextView) propositionHolder.t3);
                    textViewList.add((SizeAwareTextView) propositionHolder.t4);
                    setViewHack(textViewList);
                }

                //hack2
                {
                    final List<SizeAwareTextView> textViewList = new ArrayList<>();
                    textViewList.add((SizeAwareTextView) propositionHolder.tvInfoFirst);
                    textViewList.add((SizeAwareTextView) propositionHolder.tvInfoRepeat);
                    textViewList.add((SizeAwareTextView) propositionHolder.tvInfoRates);
                    textViewList.add((SizeAwareTextView) propositionHolder.tvInfoDays);
                    setViewHack(textViewList);
                }

                propositionHolder.tvInfoFirst.setText(pr.getCredit());
                propositionHolder.tvInfoRepeat.setText(pr.getCreditRepeat());

                propositionHolder.erbRating.setRating(pr.getScore());

                propositionHolder.tvInfoDays.setText(returnDays);

                propositionHolder.tvVerified.setVisibility(pr.isVerified() ? View.VISIBLE : View.GONE);
            } else {
                final List<SizeAwareTextView> textViewList = new ArrayList<>();
                textViewList.add((SizeAwareTextView) propositionHolder.tvMins);
                textViewList.add((SizeAwareTextView) propositionHolder.tvRating);
                setViewHack(textViewList);

                propositionHolder.btnNext.setVisibility(TextUtils.isEmpty(pr.getUrl()) ? View.GONE : View.VISIBLE);

                propositionHolder.tvRating.setText((int) Math.floor(pr.getScore()) + " / 5");
                propositionHolder.tvMins.setText(pr.getTime2Accept());

                propositionHolder.tvInfoFirst.setText(pr.getCredit() + " " + propositionHolder.tvInfoFirst.getResources().getString(R.string.curr));
                propositionHolder.tvInfoRepeat.setText(pr.getCreditRepeat() + " "
                        + propositionHolder.tvInfoFirst.getResources().getString(R.string.curr));

                propositionHolder.tvInfoDays.setText(propositionHolder.tvInfoFirst.getResources().getString(R.string.from)
                        + " " + returnDays  );
            }

            propositionHolder.tvInfoRates.setText(pr.getRates());

            propositionHolder.tvInfoTxtDays.setText(propositionHolder.tvInfoDays.getResources().getString(daysStr));

            if (pr.getType() == AdapterModel.Type.TYPE_DEF_ITEM && propositionHolder.expandableTextView != null) {
                if (!TextUtils.isEmpty(pr.getDescription())) {
                    propositionHolder.expandableTextView.setText(pr.getDescription());
                    propositionHolder.expandableTextView.setOnExpandStateChangeListener(
                            (textView, isExpanded) -> propositionHolder.btnDetails.setText(!isExpanded
                                    ? R.string.show_details : R.string.hide_details));

                    propositionHolder.btnDetails.setOnClickListener(v -> {
                        propositionHolder.collapse.performClick();
                    });

                    propositionHolder.expandableTextView.setVisibility(View.VISIBLE);
                    propositionHolder.btnDetails.setVisibility(View.VISIBLE);
                } else {
                    propositionHolder.expandableTextView.setVisibility(View.GONE);
                    propositionHolder.btnDetails.setVisibility(View.GONE);
                }

          //      propositionHolder.btnApply.setVisibility(TextUtils.isEmpty(pr.getUrl()) ? View.GONE : View.VISIBLE);
            }
        } else if (holder instanceof HeaderHolder) {
            HeaderHolder headerHolder = (HeaderHolder) holder;
            TitleModel title = (TitleModel) getItem(position);
            headerHolder.tvTitle.setText(title.getValue());
        }
    }

    @Override
    public int getItemCount() {
        int count = data != null ? data.size() : 0;
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getType().ordinal();
    }

    private AdapterModel getItem(int position) {
        return data.get(position);
    }

    private void setViewHack(List<SizeAwareTextView> textViewList) {

        SizeAwareTextView.OnTextSizeChangedListener onTextSizeChangedListener = new SizeAwareTextView.OnTextSizeChangedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onTextSizeChanged(SizeAwareTextView view, float textSize) {
                for (SizeAwareTextView textView : textViewList) {
                    if (!textView.equals(view) && textView.getTextSize() != view.getTextSize()) {
                        textView.setAutoSizeTextTypeUniformWithPresetSizes(new int[]{(int) textSize}, TypedValue.COMPLEX_UNIT_PX);
                    }
                }
            }
        };

        for (SizeAwareTextView textView : textViewList) {
            textView.setOnTextSizeChangedListener(onTextSizeChangedListener);
        }
    }

    public static class PropositionHolder extends RecyclerView.ViewHolder {

        View itemRoot;
        ImageView ivLogo;
        RatingBar erbRating;
        TextView tvRating;
        TextView tvInfoFirst, tvInfoRepeat, tvInfoRates;
        TextView tvInfoDays, tvInfoTxtDays;
        TextView tvMins;
        View btnApply;
        View tvVerified;
        TextView tvDetails;
        View collapse;
        TextView btnDetails;
        ExpandableTextView expandableTextView;
        View btnNext;

        TextView t1, t2, t3, t4;

        public PropositionHolder(@NonNull View itemView) {
            super(itemView);
            itemRoot = itemView.findViewById(R.id.itemRoot);
            ivLogo = itemView.findViewById(R.id.ivLogo);
            erbRating = itemView.findViewById(R.id.rbRating);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvInfoFirst = itemView.findViewById(R.id.tvInfoFirst);
            tvInfoRepeat = itemView.findViewById(R.id.tvInfoRepeat);
            tvInfoRates = itemView.findViewById(R.id.tvInfoRates);
            tvInfoDays = itemView.findViewById(R.id.tvInfoDays);
            tvInfoTxtDays = itemView.findViewById(R.id.tvInfoTxtDays);
            tvMins = itemView.findViewById(R.id.tvMins);
            btnApply = itemView.findViewById(R.id.btnApply);
            tvVerified = itemView.findViewById(R.id.verified);
            tvDetails = itemView.findViewById(R.id.expandable_text);
            collapse = itemView.findViewById(R.id.expand_collapse);
            btnDetails = itemView.findViewById(R.id.btnDetails);
            expandableTextView = itemView.findViewById(R.id.expand_text_view);
            btnNext = itemView.findViewById(R.id.btnNext);
            t1 = itemView.findViewById(R.id.t1);
            t2 = itemView.findViewById(R.id.t2);
            t3 = itemView.findViewById(R.id.t3);
            t4 = itemView.findViewById(R.id.t4);
        }
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;

        public HeaderHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }

    public static class FooterHolder extends RecyclerView.ViewHolder {
        public FooterHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface ICallback {
        void onClick(CRCUaProposition CRCUaProposition);
    }
}
