package com.seattlesolvers.solverslib.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ParallelDeadlineGroupTest {

    @Mock
    private Command mockDeadline;

    @Mock
    private Command mockOtherComand;

    private ParallelDeadlineGroup target;

    @Before
    public void init() {
        target = new ParallelDeadlineGroup(mockDeadline, mockOtherComand);
    }

    @Test
    public void groupInitCallsInitializeOnCommands() {
        target.initialize();

        verify(mockDeadline).initialize();
        verify(mockOtherComand).initialize();
    }

    @Test
    public void groupExecuteDoesntCallEndOnDeadlineTask() {
        when(mockOtherComand.isFinished()).thenReturn(true);

        target.initialize();
        target.execute();

        verify(mockDeadline).execute();
        verify(mockDeadline, never()).isFinished();
        verify(mockDeadline, never()).end(anyBoolean());
        verify(mockOtherComand).execute();
        verify(mockOtherComand).isFinished();
        verify(mockOtherComand).end(false);
    }

    @Test
    public void groupEndResultsInUninterruptedEndToDeadline() {
        when(mockOtherComand.isFinished()).thenReturn(false);

        target.initialize();
        target.execute();

        verify(mockDeadline, never()).end(anyBoolean());
        verify(mockOtherComand, never()).end(anyBoolean());

        target.end(false);

        verify(mockDeadline).end(false);
        verify(mockOtherComand).end(true);
    }

    @Test
    public void groupEndProducesInterruptedEndToDeadline() {
        when(mockOtherComand.isFinished()).thenReturn(false);

        target.initialize();
        target.execute();

        verify(mockDeadline, never()).end(anyBoolean());
        verify(mockOtherComand, never()).end(anyBoolean());

        target.end(true);

        verify(mockDeadline).end(true);
        verify(mockOtherComand).end(true);
    }
}
