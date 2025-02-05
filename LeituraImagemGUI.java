import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LeituraImagemGUI {
    private BufferedImage imagem, imagemOriginal, imagemAtual;  // Variável para armazenar a imagem
    private JLabel imagemLabel;    // JLabel para exibir a imagem
    int limiar = 0; //limiar do pixel binario
    int pixel = 0;
    private boolean imagemCarregada = false;
    JButton salvar = null;
    JButton histograma = null;
    JFrame frame = null;

    public void Interface() {
        // Criação da janela principal
        frame = new JFrame("Leitor de Imagem");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);  // Aumentando o tamanho da janela para acomodar a imagem

        // Layout para a janela
        frame.setLayout(new BorderLayout());

        // Painel superior para os botões
        JPanel painelSuperior = new JPanel();
        JPanel painelSair = new JPanel();
        painelSuperior.setLayout(new FlowLayout(FlowLayout.LEFT));  // Alinha os botões à esquerda
        painelSair.setLayout(new FlowLayout(FlowLayout.RIGHT));

        // Painel para mostrar a imagem
        imagemLabel = new JLabel("", SwingConstants.CENTER);
        frame.add(imagemLabel, BorderLayout.CENTER);  // Adicionando o JLabel no centro da janela

        histograma = new JButton("Histograma");
        histograma.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (imagemAtual == null) {
                        JOptionPane.showMessageDialog(null, "Nenhuma imagem foi carregada ou processada.");
                        return;
                    }

                    // Calcular o histograma
                    int[] histogramaR = new int[256];
                    int[] histogramaG = new int[256];
                    int[] histogramaB = new int[256];

                    int largura = imagemAtual.getWidth();
                    int altura = imagemAtual.getHeight();

                    for (int y = 0; y < altura; y++) {
                        for (int x = 0; x < largura; x++) {
                            int pixel = imagemAtual.getRGB(x, y);
                            int r = (pixel >> 16) & 0xFF;
                            int g = (pixel >> 8) & 0xFF;
                            int b = pixel & 0xFF;
                            histogramaR[r]++;
                            histogramaG[g]++;
                            histogramaB[b]++;
                        }
                    }

                    // Exibir o histograma em uma nova janela
                    JFrame janela = new JFrame("Histograma de Imagem");
                    ExibirHistograma painel = new ExibirHistograma(histogramaR, histogramaG, histogramaB);
                    janela.add(painel);
                    janela.setSize(800, 400);
                    janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Alterar para fechar apenas a janela de histograma
                    janela.setVisible(true);
                }
            });

        //botao salvar
        salvar = new JButton("Salvar imagem");
        salvar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (imagemAtual == null) {
                        JOptionPane.showMessageDialog(null, "Nenhuma imagem foi carregada ou processada.");
                        return;
                    }
                    // Chama o método de salvar imagem
                    salvarImagem();
                }
            });  

        // Botão para carregar a imagem
        JButton carregarImagemButton = new JButton("Carregar Imagem");
        carregarImagemButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    carregarImagem(imagemLabel);
                }
            });

        JButton sair = new JButton("Sair do Programa");
        sair.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0); //sair do programa
                }
            });

        // ComboBox para selecionar o filtro da imagem
        String[] filtros = {"Imagem Original", "Filtro Cinza", "Filtro Binario", "BlockFilter", "TritoneFilter"};
        JComboBox<String> filtroComboBox = new JComboBox<>(filtros);  
        filtroComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String filtroSelecionado = (String) filtroComboBox.getSelectedItem();
                    if(!imagemCarregada){
                        JOptionPane.showMessageDialog(null, "Nenhuma imagem carregada anteriormente");
                    }else{
                        if(filtroSelecionado == "Imagem Original"){
                            imagemOriginal(); 
                        }else if(filtroSelecionado == "Filtro Cinza"){
                            aplicarFiltroCinza();
                        }else if(filtroSelecionado == "Filtro Binario"){
                            boolean entradaValida = false;
                            while(!entradaValida){
                                String num = JOptionPane.showInputDialog(null, "Digite um numero entre 0 e 255:");

                                try{
                                    limiar = Integer.parseInt(num);
                                    if((limiar < 0) || (limiar > 255)){
                                        JOptionPane.showInputDialog(null, "Numero fora do intervalo, digite novamente:\n"); 
                                    }else{
                                        entradaValida = true;
                                        applyBinarioFilter();
                                    }
                                }catch(NumberFormatException ex){
                                    JOptionPane.showMessageDialog(null,"Entrada inválida! Por favor, insira um número inteiro.");
                                }
                            }
                        }else if(filtroSelecionado == "BlockFilter"){
                            boolean entradaValida = false;
                            while(!entradaValida){
                                String num = JOptionPane.showInputDialog(null, "Digite um numero entre 8 e 50:");

                                try{
                                    pixel = Integer.parseInt(num);
                                    if((pixel < 8) || (pixel > 50)){
                                        JOptionPane.showInputDialog(null, "Numero fora do intervalo, digite novamente:\n"); 
                                    }else{
                                        entradaValida = true;
                                        aplicarBlockFilter();
                                    }
                                }catch(NumberFormatException ex){
                                    JOptionPane.showMessageDialog(null,"Entrada inválida! Por favor, insira um número inteiro.");
                                }
                            }
                        }else if(filtroSelecionado == "TritoneFilter"){
                            aplicarTritoneFilter();
                        }
                    }
                }
            });    

        // Adicionando os botões ao painel superior
        painelSuperior.add(carregarImagemButton);
        painelSuperior.add(new JLabel("Escolha um filtro:"));
        painelSuperior.add(filtroComboBox);
        painelSair.add(histograma);
        painelSair.add(salvar);
        painelSair.add(sair);

        // Adiciona o painel superior à janela
        frame.add(painelSair, BorderLayout.SOUTH);
        frame.add(painelSuperior, BorderLayout.NORTH);

        frame.setVisible(true);
    }

    // Função para abrir o JFileChooser e carregar a imagem
    private void carregarImagem(JLabel imagemLabel) {
        // Criação do JFileChooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione uma imagem");

        // Filtra arquivos de imagem
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos de Imagem", "jpg", "jpeg", "png", "bmp", "gif"));

        // Mostra o diálogo de seleção de arquivo
        int resultado = fileChooser.showOpenDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            // Obtém o arquivo selecionado
            File arquivoImagem = fileChooser.getSelectedFile();

            // Tenta carregar a imagem
            try {
                imagem = ImageIO.read(arquivoImagem);  // Carrega a imagem
                imagemOriginal = imagem;
                imagemAtual = imagem;
                imagemCarregada = true;
                if (imagem != null) {
                    // Cria um ícone para mostrar a imagem
                    ImageIcon imagemIcon = new ImageIcon(imagem);
                    imagemLabel.setIcon(imagemIcon);  // Define o ícone no JLabel
                    imagemLabel.repaint();  // Repaint para garantir que a imagem seja exibida
                } else {
                    System.out.println("Não foi possível carregar a imagem.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Erro ao carregar a imagem.");
            }
        }
    }

    //faz a imagem original aparecer de novo na tela
    private void imagemOriginal(){
        if(imagemOriginal != null){
            imagem = imagemOriginal;
            imagemLabel.setIcon(new ImageIcon(imagem));
            imagemLabel.repaint();
            imagemAtual = imagemOriginal;
        }else{
            JOptionPane.showMessageDialog(null, "Nenhuma imagem carregada anteriormente");
        }
    }

    // Método para aplicar o filtro de escala de cinza
    private void aplicarFiltroCinza() {
        BufferedImage grayImage = null;
        if (imagem != null) {
            grayImage = Filtros.FiltroCinza(imagem);
            imagemLabel.setIcon(new ImageIcon(grayImage));
            imagemLabel.repaint();
        }
        imagemAtual = grayImage;
    }

    // Método para aplicar o filtro pixelado
    private void aplicarBlockFilter() {
        BufferedImage pixelImage = null;
        if (imagem != null) {
            pixelImage = Filtros.applyPixelation(imagem, pixel);
            imagemLabel.setIcon(new ImageIcon(pixelImage));
            imagemLabel.repaint();
        }
        imagemAtual = pixelImage;
    }

    // Método para aplicar o filtro tritone(3 tons)
    private void aplicarTritoneFilter() {
        BufferedImage tritoneImage = null;
        if (imagem != null) {
            tritoneImage = Filtros.applyTritoneFilter(imagem,  new Color(0, 0, 50), new Color(0, 0, 255), new Color(173, 216, 230));
            imagemLabel.setIcon(new ImageIcon(tritoneImage));
            imagemLabel.repaint();
        }
        imagemAtual = tritoneImage;
    }

    // Método para aplicar o filtro de inverter cores
    private void applyBinarioFilter() {
        BufferedImage invertedImage = null;
        if (imagem != null) {
            invertedImage = Filtros.aplicarFiltroBinario(imagem, limiar);
            imagemLabel.setIcon(new ImageIcon(invertedImage));
            imagemLabel.repaint();
        }
        imagemAtual = invertedImage;
    }

    private void salvarImagem() {
        if (imagem == null) {
            JOptionPane.showMessageDialog(null, "Não há imagem para salvar.");
            return;
        }

        // Criação do JFileChooser para salvar o arquivo
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Imagem");

        // Filtra arquivos de imagem
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos de Imagem", "jpg", "jpeg", "png", "bmp", "gif"));

        // Mostra o diálogo de salvar
        int resultado = fileChooser.showSaveDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            // Obtém o arquivo escolhido
            File arquivoImagem = fileChooser.getSelectedFile();

            // Verifica a extensão do arquivo e adiciona se necessário
            String nomeArquivo = arquivoImagem.getAbsolutePath();
            if (!nomeArquivo.endsWith(".png") && !nomeArquivo.endsWith(".jpg") && !nomeArquivo.endsWith(".jpeg")) {
                nomeArquivo += ".png";  // Adiciona uma extensão padrão (pode ser .jpg, .png, etc)
                arquivoImagem = new File(nomeArquivo);
            }

            try {
                // Salva a imagem no arquivo escolhido
                ImageIO.write(imagemAtual, "PNG", arquivoImagem);  // Você pode mudar o formato para "JPG" ou outro formato
                JOptionPane.showMessageDialog(null, "Imagem salva com sucesso!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Erro ao salvar a imagem: " + ex.getMessage());
            }
        }
    }
}