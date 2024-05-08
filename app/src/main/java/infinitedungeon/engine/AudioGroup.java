package infinitedungeon.engine;

import com.sun.media.jfxmedia.MediaException;
import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class AudioGroup {

    private final ArrayList<SimpleEntry<String, Boolean>> toAdd;
    private final ArrayList<MediaPlayer> audios;
    private double volume = 0;
    private boolean init;

    public boolean isInit() {
        return init;
    }

    void setInit(boolean init) {
        this.init = init;
        if (init) {
            for (SimpleEntry<String, Boolean> se : toAdd) {
                playAudio(se.getKey(), se.getValue());
            }
            toAdd.clear();
        }
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double value) {
        if (value >= 0.0 && value <= 1.0) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    volume = value;
                    for (MediaPlayer mp : audios) {
                        mp.setVolume(volume);
                    }
                }
            });
        }
    }

    public AudioGroup() {
        audios = new ArrayList<>();
        toAdd = new ArrayList<>();
    }

    public void playAudio(String path, boolean repeat) throws MediaException {
        if (init) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Media media = new Media(new File(path).toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    if (volume <= 1.0) {
                        mediaPlayer.setVolume(volume);
                    }
                    mediaPlayer.setOnEndOfMedia(new Runnable() {
                        @Override
                        public void run() {
                            if (repeat) {
                                mediaPlayer.seek(Duration.ZERO);
                            } else {
                                audios.remove(mediaPlayer);
                            }
                        }
                    });
                    mediaPlayer.setOnStopped(new Runnable() {
                        @Override
                        public void run() {
                            audios.remove(mediaPlayer);
                        }
                    });
                    audios.add(mediaPlayer);
                    mediaPlayer.play();
                }
            });
        } else {
            toAdd.add(new SimpleEntry<>(path, repeat));
        }
    }

    public void pauseAudios() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (MediaPlayer mp : audios) {
                    mp.pause();
                }
            }
        });
    }

    public void resumeAudios() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (MediaPlayer mp : audios) {
                    mp.play();
                }
            }
        });

    }

    public void stopAudios() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (MediaPlayer mp : audios) {
                    mp.stop();
                }
            }
        });

    }
}
