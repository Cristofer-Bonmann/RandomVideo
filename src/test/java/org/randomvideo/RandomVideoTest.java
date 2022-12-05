package org.randomvideo;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class RandomVideoTest {

  @Spy
  private RandomVideo randomVideo;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void naoDeveExistirVideosParaSortear() {
    final File directory = new File(Sistema.DEFAULT_PATH);
    final List<File> files = new ArrayList<>();
    final String msg = String.format("Nenhum arquivo de vídeo foi encontrado em %s", directory.getAbsolutePath());

    doReturn(files).when(randomVideo).listVideoFiles(directory);
    doNothing().when(randomVideo).notificar(msg);
    randomVideo.sortearVideo();

    verify(randomVideo).listVideoFiles(directory);
    verify(randomVideo).notificar(msg);
  }

  @Test
  public void deveDispararExcecaoAoExecutarVideoSorteado() throws IOException, InterruptedException {
    final IOException ioException = new IOException("");

    doThrow(ioException).when(randomVideo).executarVideo(Mockito.any());
    doNothing().when(randomVideo).notificarFalha(Mockito.any(), Mockito.any());
    randomVideo.sortearVideo();

    verify(randomVideo).executarVideo(Mockito.any());
    verify(randomVideo).notificarFalha(Mockito.any(), Mockito.any());
  }

  @Test
  public void deveExecutarVideoSorteado() throws IOException, InterruptedException {
    doNothing().when(randomVideo).executarVideo(Mockito.any());
    randomVideo.sortearVideo();

    verify(randomVideo).executarVideo(Mockito.any());
  }

  @Test
  public void naoDeveSortearRVideoComListaInvalida() {
    List<RVideo> rVideos = new ArrayList();

    RVideo rVideo = randomVideo.sortearRVideo(rVideos);

    assertThat(rVideo.getIndex(), is(-1));
    assertThat(rVideo.getAbsolutePath(), is(""));

    rVideos = null;

    rVideo = randomVideo.sortearRVideo(rVideos);

    assertThat(rVideo.getIndex(), is(-1));
    assertThat(rVideo.getAbsolutePath(), is(""));
  }

  @Test
  public void deveSortearRVideo() {
    final List<File> files = Arrays.asList(new File(Sistema.DEFAULT_PATH).listFiles());
    final List<RVideo> listRVideos = randomVideo.gerarListaRVideo(files);

    final RVideo rVideo = randomVideo.sortearRVideo(listRVideos);

    assertThat(rVideo, notNullValue());
  }

  @Test
  public void deveGerarListaRVideo() {
    final List<File> files = Arrays.asList(new java.io.File(Sistema.DEFAULT_PATH).listFiles());

    final List<RVideo> rVideos = randomVideo.gerarListaRVideo(files);

    assertThat(rVideos.get(0).getAbsolutePath().contains("1-arquivo_de_video_1.mp4"), is(true));
    assertThat(rVideos.get(1).getAbsolutePath().contains("2-arquivo_de_video_2.mkv"), is(true));
    assertThat(rVideos.get(2).getAbsolutePath().contains("3-arquivo_de_video_3.flv"), is(true));
    assertThat(rVideos.get(3).getAbsolutePath().contains("4-arquivo_de_video_4.webm"), is(true));
  }

  @Test
  public void naoDeveSortearVideoComListaDeFilesInvalida() throws IOException, InterruptedException {
    final File defaultPath = new File(Sistema.DEFAULT_PATH);
    final List<File> listFiles = null;

    doReturn(listFiles).when(randomVideo).listVideoFiles(defaultPath);
    randomVideo.sortearVideo();

    verify(randomVideo).sortearVideo();
    verify(randomVideo, times(0)).executarVideo(Mockito.any());
  }

  @Test
  public void naoDeveSortearVideoComListaDeFilesVazia() throws IOException, InterruptedException {
    final File defaultPath = new File(Sistema.DEFAULT_PATH);
    final List<File> listFiles = new ArrayList<>();

    doReturn(listFiles).when(randomVideo).listVideoFiles(defaultPath);
    randomVideo.sortearVideo();

    verify(randomVideo).sortearVideo();
    verify(randomVideo, times(0)).executarVideo(Mockito.any());
  }

  @Test
  public void deveListarArquivosDeVideo() {
    final String pathName = Sistema.DEFAULT_PATH;
    final File fileDirectory = new File(pathName);

    final Collection<File> files = randomVideo.listVideoFiles(fileDirectory);

    List<File> fileList = files.stream().collect(Collectors.toList());
    assertThat(fileList.get(0).getName(), is("1-arquivo_de_video_1.mp4"));
    assertThat(fileList.get(1).getName(), is("2-arquivo_de_video_2.mkv"));
    assertThat(fileList.get(2).getName(), is("3-arquivo_de_video_3.flv"));
    assertThat(fileList.get(3).getName(), is("4-arquivo_de_video_4.webm"));
  }
}
