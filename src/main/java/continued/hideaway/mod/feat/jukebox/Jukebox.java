package continued.hideaway.mod.feat.jukebox;

import continued.hideaway.mod.HideawayContinued;
import continued.hideaway.mod.Prompt;
import continued.hideaway.mod.feat.lifecycle.Task;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class Jukebox {
    public long trackPointer = -1;
    public int partPointer = -1;
    public JukeboxTrack currentTrack = null;
    public JukeboxTrack.Part currentPart = null;

    public boolean looping = false;

    public Jukebox() {
        HideawayContinued.lifecycle().add(
            Task.of(() -> {
                // Nested if statements are purely because it'll crash if one of them is null
                if (!looping) {
                    if (HideawayContinued.client() != null) {
                        if (HideawayContinued.connected()) {
                            // Java needs deconstructors 😭
                            if (HideawayContinued.jukebox() != null) {
                                if (currentTrack != null && currentPart != null) {
                                    if (trackPointer >= currentPart.length) {
                                        loop();
                                    } else trackPointer++;
                                }
                            }
                        }
                    }
                }
            }, 0)
        );
    }

    public void play(JukeboxTrack track) {
        HideawayContinued.client().getSoundManager().stop();
        currentTrack = track;
        currentPart = track.parts.get(0);
        trackPointer = 0;
        partPointer = 0;
        HideawayContinued.client().player.playNotifySound(
                SoundEvent.createVariableRangeEvent(currentTrack.parts.get(partPointer).id), SoundSource.MASTER, 1, 1
        );
    }

    private void loop() {
        looping = true;
        JukeboxTrack temp = currentTrack;
        stop();
        currentTrack = temp;

        partPointer++;
        trackPointer = 0;

        try {
            currentPart = currentTrack.parts.get(partPointer);
        } catch (Exception e) {
            partPointer = 0;
            currentPart = currentTrack.parts.get(partPointer);
        }
        try {
            HideawayContinued.client().player.playNotifySound(
                    SoundEvent.createVariableRangeEvent(currentTrack.parts.get(partPointer).id), SoundSource.MASTER, 1, 1
            );
        } catch (Exception e) {
            Prompt.error("An issue occurred when looping music with the Jukebox. Please send your latest.log file to the developers of Hideaway+.");
        }
        looping = false;
    }

    public void stop() {
        HideawayContinued.client().getSoundManager().stop();
        currentTrack = null;
        currentPart = null;
        trackPointer = -1;
        partPointer = -1;
    }
}
