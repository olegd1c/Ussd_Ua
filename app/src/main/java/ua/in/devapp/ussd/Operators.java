package ua.in.devapp.ussd;

public enum Operators {
    ks(1,R.string.ks_title),
    life(2,R.string.life_title),
    mts(3,R.string.mts_title);

    private int id;
    private int title;

    public int getId() {
        return id;
    }

    public int getTitle() {
        return title;
    }

    Operators(int id, int title) {
         this.id = id;
         this.title = title;
    }
}