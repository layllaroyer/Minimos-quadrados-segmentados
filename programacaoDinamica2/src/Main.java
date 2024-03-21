import java.util.ArrayList;
import java.util.Stack;
import java.util.Collections;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) {
        ArrayList<Ponto> pontos = new ArrayList<>();
        double minimo;
        pontos.add(new Ponto(2.44, 4.12));
        pontos.add(new Ponto(2.23, 3.77));
        pontos.add(new Ponto(1.76, 2.35));
        pontos.add(new Ponto(1.9, 3));
        pontos.add(new Ponto(2.05, 3.56));
        ordenarPontosX(pontos);

        minimo = segmentedLeastSquares(pontos);
        System.out.println("Custo da solução ótima: "+minimo);
    }

    public static double segmentedLeastSquares(ArrayList<Ponto> pontos) {
        int C=1; //custo para criar um segmento novo
        int n = pontos.size();
        double[] acumulativoX = new double[n + 1];
        double[] acumulativoY = new double[n + 1];
        double[] acumulativoXY = new double[n + 1];
        double[] acumulativoXX = new double[n + 1];

        double somaX = 0;
        double somaY = 0;
        double somaXY = 0;
        double somaXX = 0;
        int intervalo;
        double numerador;
        double denominador;
        double temp;
        double[][] a = new double[n + 1][n + 1];
        double[][] b = new double[n + 1][n + 1];
        double[][] errosMinimos = new double[n + 1][n + 1];
        int k;
        double[] OPT = new double[n + 1];
        int[] segmento_otimo = new int[n + 1];

        acumulativoX[0] = 0;
        acumulativoY[0] = 0;
        acumulativoXY[0] = 0;
        acumulativoXX[0] = 0;
        for (int j = 1; j <= n; j++) {
            acumulativoX[j] = acumulativoX[j - 1] + pontos.get(j - 1).getX();
            acumulativoY[j] = acumulativoY[j - 1] + pontos.get(j - 1).getY();
            acumulativoXX[j] = acumulativoX[j - 1] + pontos.get(j - 1).getX() * pontos.get(j - 1).getX();
            acumulativoXY[j] = acumulativoY[j - 1] + pontos.get(j - 1).getY() * pontos.get(j - 1).getX();

            for (int i = 1; i <= j; i++) {
                intervalo = j - i + 1;
                somaX = acumulativoX[j] - acumulativoX[i - 1];
                somaY = acumulativoY[j] - acumulativoY[i - 1];
                somaXX = acumulativoXX[j] - acumulativoXX[i - 1];
                somaXY = acumulativoXY[j] - acumulativoXY[i - 1];

                numerador = intervalo * somaXY - somaX * somaY;
                if (numerador == 0)
                    a[i][j] = 0;
                else {
                    denominador = intervalo * somaXX - somaX * somaX;
                    a[i][j] = (denominador == 0) ? Double.POSITIVE_INFINITY : (numerador / denominador);
                }
                b[i][j] = (somaY - a[i][j] * somaX) / intervalo;

                for (k = i, errosMinimos[i][j] = 0; k <= j; k++) {
                    temp = pontos.get(k - 1).getY() - a[i][j] * pontos.get(k - 1).getX() - b[i][j];
                    errosMinimos[i][j] += temp * temp;
                }

            }

        }

        OPT[0] = segmento_otimo[0] = 0;
        for (int j = 1; j <= n; j++) {
            double mn = Double.POSITIVE_INFINITY;
            int i;
            for (i = 1, k = 0; i <= j; i++) {
                temp = errosMinimos[i][j] + OPT[i - 1];
                if (temp < mn) {
                    mn = temp;
                    k = i;
                }
            }
            OPT[j] = mn+C;
            segmento_otimo[j] = k;
        }
        solucaoOtima(segmento_otimo, a, b, errosMinimos, n);

        return OPT[n];
    }

    public static void solucaoOtima(int []segmento_otimo, double [][]a, double [][]b, double [][]errosMinimos, int n){
        Stack<Integer> segments = new Stack<>();
        for (int i = n, j = segmento_otimo[n]; i > 0; i = j - 1, j = segmento_otimo[i]) {
            segments.push(i);
            segments.push(j);
        }
        int i, j;
        System.out.println("\nSolucao otima:\n");
        while (!segments.empty()) {
            i = segments.pop();
            j = segments.pop();
            System.out.printf("Segmento (y = %f * x + %f) dos pontos %d a %d com erro quadratico %f.\n",
                    a[i][j], b[i][j], i, j, errosMinimos[i][j]);
        }
        System.out.println(" ");
    }

    public static void ordenarPontosX(ArrayList<Ponto> pontos) {
        // Usa Collections.sort com um comparador personalizado
        Collections.sort(pontos, new Comparator<Ponto>() {
            @Override
            public int compare(Ponto ponto1, Ponto ponto2) {
                return Double.compare(ponto1.getX(), ponto2.getX());
            }
        });
    }
}
