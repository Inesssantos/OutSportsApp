package com.example.outsports.data;

public class Conversion {

    private final float conversion_kg_lb;
    private final float conversion_cm_ft;
    private final float conversion_km_mi;

    public Conversion() {
        conversion_kg_lb = Constants.CONVERSION_KG_LB;
        conversion_cm_ft = Constants.CONVERSION_CM_FT;
        conversion_km_mi = Constants.CONVERSION_KM_MI;
    }

    public float cm_to_ft(float cm) {
        return (cm * conversion_cm_ft);
    }

    public float ft_to_cm(float ft) {
        return (ft / conversion_cm_ft);
    }

    public float kg_to_lb(float kg) {
        return (kg * conversion_kg_lb);
    }

    public float lb_to_kg(float lb) {
        return (lb / conversion_kg_lb);
    }

    public float km_to_mi(double km) {
        return (float) (km * conversion_km_mi);
    }

    public float mi_to_km(float mi) {
        return (mi / conversion_km_mi);
    }
}
