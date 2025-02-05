import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;

/**
 * Escreva uma descrição da classe ExibirHistograma aqui.
 * 
 * @author (seu nome) 
 * @version (um número da versão ou uma data)
 */
public class ExibirHistograma extends JPanel {
    private int[] histogramaR;
    private int[] histogramaG;
    private int[] histogramaB;

    public ExibirHistograma(int[] histogramaR, int[] histogramaG, int[] histogramaB) {
        this.histogramaR = histogramaR;
        this.histogramaG = histogramaG;
        this.histogramaB = histogramaB;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Desenhar o histograma para o canal R (Vermelho)
        g.setColor(Color.RED);
        for (int i = 0; i < 256; i++) {
            g.drawLine(i, 300, i, 300 - histogramaR[i] / 10); // Escalonar para que o gráfico caiba
        }

        // Desenhar o histograma para o canal G (Verde)
        g.setColor(Color.GREEN);
        for (int i = 0; i < 256; i++) {
            g.drawLine(i, 300, i, 300 - histogramaG[i] / 10);
        }

        // Desenhar o histograma para o canal B (Azul)
        g.setColor(Color.BLUE);
        for (int i = 0; i < 256; i++) {
            g.drawLine(i, 300, i, 300 - histogramaB[i] / 10);
        }
    }
}
