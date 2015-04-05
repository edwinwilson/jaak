package io.sarl.jaak.kernel.internal;

import io.sarl.jaak.kernel.internal.AbstractStampedEvent;
import io.sarl.lang.annotation.Generated;

/**
 * Notify the simulation engine that an
 * agent want to be synchronized.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class SynchronizeBody extends AbstractStampedEvent {
  public SynchronizeBody(final float ct, final float lsd) {
    super(ct, lsd);
  }
  
  @Generated
  private final static long serialVersionUID = -807349986L;
}
