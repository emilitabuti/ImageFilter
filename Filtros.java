import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Escreva uma descrição da classe Filtros aqui.
 * 
 * @author (seu nome) 
 * @version (um número da versão ou uma data)
 */
public class Filtros
{
    // Função para aplicar o filtro de cinza
    public static BufferedImage FiltroCinza(BufferedImage imagem) {
        int largura = imagem.getWidth();
        int altura = imagem.getHeight();
        
        // Criar uma nova imagem com o mesmo tamanho da imagem original
        BufferedImage imagemCinza = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < largura; x++) {
            for (int y = 0; y < altura; y++) {
                // Obter o valor de cor do pixel
                Color corOriginal = new Color(imagem.getRGB(x, y));
                
                // Calcular o valor de cinza usando a fórmula de luminosidade
                int cinza = (int) (0.299 * corOriginal.getRed() + 0.587 * corOriginal.getGreen() + 0.114 * corOriginal.getBlue());
                
                // Criar um novo objeto Color para o tom de cinza e definir o novo valor do pixel
                Color corCinza = new Color(cinza, cinza, cinza);
                imagemCinza.setRGB(x, y, corCinza.getRGB());
            }
        }

        return imagemCinza;
    }
    
    public static BufferedImage aplicarFiltroBinario(BufferedImage imagem, int limiar) {
        int largura = imagem.getWidth();
        int altura = imagem.getHeight();
        
        // Criar uma nova imagem para armazenar o resultado
        BufferedImage imagemBinaria = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < largura; x++) {
            for (int y = 0; y < altura; y++) {
                // Obter o valor de cor do pixel original
                int rgb = imagem.getRGB(x, y);
                
                // Obter os componentes de cor do pixel
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                // Calcular o valor de cinza usando a fórmula de luminosidade
                int cinza = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                
                // Se o valor de cinza for maior que o limiar, definir o pixel como branco (255), caso contrário, preto (0)
                Color corBinaria;
                if (cinza >= limiar) {
                    corBinaria = new Color(255, 255, 255); // Branco
                } else {
                    corBinaria = new Color(0, 0, 0); // Preto
                }
                
                // Definir o valor do pixel na nova imagem
                imagemBinaria.setRGB(x, y, corBinaria.getRGB());
            }
        }
        
        return imagemBinaria;
    }
    
     public static BufferedImage applyPixelation(BufferedImage image, int blockSize) {
        // Obter as dimensões da imagem original
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Criar uma nova imagem para aplicar o efeito
        BufferedImage pixelatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        // Percorrer a imagem em blocos
        for (int y = 0; y < height; y += blockSize) {
            for (int x = 0; x < width; x += blockSize) {
                // Calcular a cor média do bloco
                Color averageColor = calculateAverageColor(image, x, y, blockSize);
                
                // Preencher o bloco com a cor média calculada
                for (int by = 0; by < blockSize && (y + by) < height; by++) {
                    for (int bx = 0; bx < blockSize && (x + bx) < width; bx++) {
                        pixelatedImage.setRGB(x + bx, y + by, averageColor.getRGB());
                    }
                }
            }
        }
        
        return pixelatedImage;
    }

    public static Color calculateAverageColor(BufferedImage image, int startX, int startY, int blockSize) {
        int r = 0, g = 0, b = 0;
        int count = 0;

        // Calcular a cor média dos pixels dentro do bloco
        for (int y = startY; y < startY + blockSize && y < image.getHeight(); y++) {
            for (int x = startX; x < startX + blockSize && x < image.getWidth(); x++) {
                Color pixelColor = new Color(image.getRGB(x, y));
                r += pixelColor.getRed();
                g += pixelColor.getGreen();
                b += pixelColor.getBlue();
                count++;
            }
        }

        // Média das cores
        r /= count;
        g /= count;
        b /= count;

        return new Color(r, g, b);
    }
    
    public static BufferedImage applyTritoneFilter(BufferedImage image, Color shadow, Color midtone, Color highlight) {
        // Cria uma cópia da imagem para evitar modificar a original
        BufferedImage outputImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                // Obtém a cor do pixel
                int rgb = image.getRGB(x, y);
                
                // Extrai os componentes RGB do pixel
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                
                // Converte a cor de RGB para HSB
                float[] hsb = Color.RGBtoHSB(red, green, blue, null);
                float brightness = hsb[2]; // Obtemos o brilho (luminosidade) do pixel
                
                // Determina a cor final com base no brilho
                Color finalColor = getTritoneColor(brightness, shadow, midtone, highlight);
                
                // Define a cor no novo pixel
                outputImage.setRGB(x, y, finalColor.getRGB());
            }
        }
        
        return outputImage;
    }
    
    // Método que determina a cor a ser aplicada com base no brilho do pixel
    private static Color getTritoneColor(float brightness, Color shadow, Color midtone, Color highlight) {
        if (brightness < 0.33) {
            return shadow; // Para baixo brilho (sombra)
        } else if (brightness < 0.66) {
            return midtone; // Para brilho médio (meio-tom)
        } else {
            return highlight; // Para brilho alto (destaque)
        }
    }
}
