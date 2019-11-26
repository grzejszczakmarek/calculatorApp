package com.example.marekwin10.calc;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView txt_bottom;
    private TextView txt_top;
    private String dispBottom="0";
    private String dispTop="";
    private CharSequence oper="";
    private Double a=0.0,b=0.0,result=0.0;
    private Boolean unlockNumber=true;
    private Boolean lockComma=false;
    private Boolean eraseTop=false;
    private Boolean isResult=false;
    private Boolean lockOneArgFunc=false;
    private int eraseCounter=0;
    private Toast kom;
    private int numberOfOperators = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale.setDefault(Locale.US);

        Bundle extras = getIntent().getExtras();//pobieram wszystkie zmienne zapisane przez putExtra
        String choice= extras.getString("layout");
        kom = new Toast(this);
        int orientation = this.getResources().getConfiguration().orientation;
        if(choice.equals("Prosty"))setContentView(R.layout.activity_main);
        if(choice.equals("Naukowy")) {
            if(orientation == Configuration.ORIENTATION_PORTRAIT)setContentView(R.layout.activity_main_complex);
            else setContentView(R.layout.activity_main_complexrotated);
        }
        //Wykorzustując funkcje putExtra w klasie Menu jestem w stanie przeslac daną do tej klasy i wybrac layout dla odpowiedniego kalkulatora
        //Unikam w ten sposób duplikacji kodu

        txt_bottom = (TextView)findViewById(R.id.bottom_disp);
        txt_top = (TextView)findViewById(R.id.top_disp);

        if(savedInstanceState!=null)
        {
            dispBottom = savedInstanceState.getString("bottom");
            dispTop= savedInstanceState.getString("top");
            unlockNumber= savedInstanceState.getBoolean("unlockNumberTemp");
            lockComma= savedInstanceState.getBoolean("lockCommaTemp");
            oper= savedInstanceState.getCharSequence("operTemp");
            a= savedInstanceState.getDouble("aTemp");
            b= savedInstanceState.getDouble("bTemp");
        }//odczyt danych w przypadku obrotu

        if(dispBottom != null)txt_bottom.setText(dispBottom);
        if(dispTop != null)txt_top.setText(dispTop);
    }


    //zapisywanie zmiennych do obrotu
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("top", dispTop);
        outState.putString("bottom", dispBottom);
        outState.putBoolean("unlockNumberTemp", unlockNumber);
        outState.putBoolean("lockCommaTemp", lockComma);
        outState.putCharSequence("operTemp", oper);
        outState.putDouble("aTemp", a);
        outState.putDouble("bTemp", b);

    }

    //skrocenie wyswietlanego Double'a aby nie pokazywało zamiast 1 to 1.0
    public String removeZeros(Double d){
        if (d % 1.0 != 0)
            return String.format("%s", d);
        else
            return String.format("%.0f",d);
    }

    //konwersja tekstu formatowanego w textView do double
    public Double getDouble(String s){
        if(!s.equals("")) return Double.valueOf(s.replaceAll(" ", ""));
        else return 0.0;
    }

    //funkcja wyswietlajaca bledy danych
    public Boolean checkFunc(Double a){
        if(a==Double.POSITIVE_INFINITY||a==Double.NEGATIVE_INFINITY) {
            kom.cancel();kom=Toast.makeText(this,"Poza zakresem",400);kom.show();
            return false;
        }
        else if(Double.isNaN(a)) {
            kom.cancel();kom=Toast.makeText(this,"Błędne dane wejściowe",400);kom.show();
            return false;
        }
        else{
            return true;
        }
    }

    //format wprowadzanych liczb
    public static String inputFormat(Double number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(symbols);
        df.setGroupingSize(3);
        df.setMaximumFractionDigits(15);
        return df.format(number);
    }

    //format wynikowy
    public static String resultFormat(Double number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat();
        if(number<99999999.0) {
            df.setDecimalFormatSymbols(symbols);
            df.setGroupingSize(3);
            df.setMaximumFractionDigits(15);
        }else{
            df = new DecimalFormat("0.##########E0");
        }
        return df.format(number);
    }

    public void updateDisp(){
        txt_bottom.setText(dispBottom);
        txt_top.setText(dispTop);
    }

    public void resultFunc(Boolean isOneArg){

        switch(String.valueOf(oper)){
            case "+": result=a+b;break;
            case "-": result=a-b;break;
            case "*": result=a*b;break;
            case "/": if(b!=0){result=a/b;}else{kom.cancel();kom=Toast.makeText(this,"Nie dzielimy przez 0",400);kom.show();result=Double.NaN;};break;
            case "√": result=Math.sqrt(a);break;
            case "x²": result=a*a;break;
            case "xʸ": result=Math.pow(a,b);break;
            case "log": result=Math.log10(a);break;
            case "sin": result=Math.sin(Math.toRadians(a));break;
            case "cos": result=Math.cos(Math.toRadians(a));break;
            case "tan": result=Math.tan(Math.toRadians(a));break;
            case "ln": result=Math.log(a);break;
            case "%": result=a*0.01;
        }
        if(!String.valueOf(oper).equals("/"))
            checkFunc(result);

        isResult=true;//flaga ustawiana gdy funkcja wykona sie; cel to czyszczenie tekstu kiedy chcemy wpisac nowa liczbe

        if(!isOneArg)dispTop="";
        dispBottom=resultFormat(result);
        if(dispBottom.contains(".")){
            lockComma=true;
        }//jesli wynik posiada czesc ulamkowa blokuje mozliwosc postawienia przecinka
    }

    public void btn_clear_OnClick(View v){
        dispBottom = "";
        dispTop = "";
        lockComma=false;
        dispBottom="0";
        oper=null;
        result=0.0;
        numberOfOperators=0;
        updateDisp();
    }

    public void btn_BKSP_OnClick(View v){
        eraseCounter++;
        if(dispBottom.length()>0&&!dispBottom.equals("0")) {
            if(dispBottom.substring(dispBottom.length()-1).equals(".")){
                lockComma=false; //odblokowuje przecinek kiedy backspace go skasuje
            }
            dispBottom = dispBottom.substring(0, dispBottom.length() - 1);
            updateDisp();
        }
        if(dispBottom.equals("")){
            dispBottom="0";
            updateDisp();
        }//jesli index dojdzie do lewej strony, ustawia wartosc na 0
        if(eraseCounter==2||Double.isNaN(result)){
            dispBottom = "";
            dispTop = "";
            lockComma=false;
            dispBottom="0";
            oper=null;
            result=0.0;
            updateDisp();
        }//jesli przycisk wcisniety dwa razy czysci rejestr(wykonuje to samo co metoda wyzej)
    }

    public void btn_invert_OnClick(View v){
        if(checkFunc(result)) {
            if (!dispBottom.equals("0") && !dispBottom.equals("")) {
                dispBottom = removeZeros((getDouble(dispBottom)) * (-1));
                dispBottom = inputFormat(getDouble(dispBottom));
                updateDisp();
            }
        }
    }

    public void btn_comma_OnClick(View v){
        if (!lockComma&&!Double.isInfinite(getDouble(dispBottom))){
            if(dispBottom.equals("")){
                dispBottom="0";
            }
            Button comma = (Button) v;
            lockComma = true;
            dispBottom += comma.getText();
            eraseCounter=0;
            updateDisp();
        }
    }

    public void btn_Operator_OnClick(View v){
        if(numberOfOperators>0){
            a = getDouble(dispTop.substring(0,dispTop.length()-1));
            b = getDouble(dispBottom);
            resultFunc(false);
            lockOneArgFunc = false;

            Button op = (Button) v;
            oper = op.getText();

            updateDisp();
            txt_top.setText(txt_bottom.getText().toString()+oper.toString());
            dispTop = txt_bottom.getText().toString()+oper.toString();
            eraseTop = false;
            txt_bottom.setText("");

        }else{
            if(checkFunc(result)) {
                numberOfOperators++;
                Button op = (Button) v;
                oper = op.getText();
                if (unlockNumber) {
                    a = getDouble(dispBottom);
                }//zapisuje liczbe jesli podany zostal operator i blokuje ją, jesli bysmy chcieli zmienic znak
                unlockNumber = false;
                lockComma = false;
                eraseTop = false;
                lockOneArgFunc = true;
                if (String.valueOf(oper).equals("xʸ")) {
                    dispTop = inputFormat(a) + "^";
                } else {//wyjatek do wypisywania dla X^Y
                    dispTop = inputFormat(a) + oper;
                }
                dispBottom = "";
                updateDisp();
            }
        }

    }

    public void btn_Operator_OneArg(View v){
        if(checkFunc(result)) {
            if(lockOneArgFunc){dispTop="";dispBottom="0";};
            Button op = (Button) v;
            oper = op.getText();
            a = getDouble(dispBottom);
            if(String.valueOf(oper).equals("x²")){
                dispTop ="("+dispBottom+")²";
            }//wyjatek do wypisywania dla X^2
            else {
                dispTop = oper + "(" + dispBottom + ")";
            }
            resultFunc(true);
            eraseTop=true;
            lockOneArgFunc=false;
            updateDisp();
        }
    }

    public void btn_Equal_OnClick(View v){
        if(checkFunc(result)) {
            if (!txt_top.getText().equals("") && !txt_bottom.getText().equals("")) {
                String digits = dispTop.replaceAll("[^0-9.]", "");
                a = getDouble(digits);
                b = getDouble(dispBottom);
                numberOfOperators = 0;
                resultFunc(false);
                lockOneArgFunc = false;
                updateDisp();
            }
        }
    }

    public void btn_Num_OnClick(View v){
        //if(dispBottom.length()<15) {
        eraseCounter = 0;
        Button num = (Button) v;
        if (eraseTop) {
            dispTop = "";
            eraseTop = false;
        }//gorna czesc ekranu jest czyszczona po wcisnieciu liczby w przypadku, gdy poprzednim dzialaniem bylo dzialanie jednoargumentowe
        unlockNumber = true;
        if (dispBottom.equals("0")||isResult) {
            isResult=false;
            result = 0.0;
            dispBottom = "";
            lockComma = false;
        } //zeruje wynik po dzialaniu i ustawiam mozliwosc stawiania przcinka
        if(dispBottom.length()<15) {
            dispBottom += num.getText();
        }
        dispBottom=inputFormat(getDouble(dispBottom));
        updateDisp();
        //}
    }

}
