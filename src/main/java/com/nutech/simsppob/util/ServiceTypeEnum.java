package com.nutech.simsppob.util;

public enum ServiceTypeEnum {
    PAJAK ("Pajak PBB", 40000),
    PLN ("Listrik", 10000),
    PDAM ("PDAM Berlangganan", 40000),
    PULSA ("Pulsa", 40000),
    PGN ("PGN Berlangganan", 50000),
    MUSIK ("Musik Berlangganan", 50000),
    TV ("TV Berlangganan", 50000),
    PAKET_DATA ("Paket data", 50000),
    VOUCHER_GAME ("Voucher Game", 100000),
    VOUCHER_MAKANAN ("Voucher Makanan", 100000),
    QURBAN ("Qurban", 200000),
    ZAKAT ("Zakat", 300000);

    public final String serviceName;
    public final int serviceTariff;

    ServiceTypeEnum(String serviceName, int serviceTariff) {

        this.serviceName = serviceName;
        this.serviceTariff = serviceTariff;
    }
}
