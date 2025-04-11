package stocktracker.model.enums;

public enum Unit {
    KG("кг"),
    LITER("л"),
    PIECE("шт"),
    BOX("кор"),
    PACK("уп");

    public final String translate;

    Unit(String translate) {
        this.translate = translate;
    }

    String getTranslate(){
        return translate;
    }
}
