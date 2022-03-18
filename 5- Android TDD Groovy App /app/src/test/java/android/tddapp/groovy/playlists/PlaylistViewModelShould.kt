package android.tddapp.groovy.playlists

import android.tddapp.groovy.utils.BaseUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test
import petros.efthymiou.groovy.utils.getValueForTest

class PlaylistViewModelShould : BaseUnitTest() {

    private lateinit var viewModel: PlaylistViewModel
    private val mockRepository: PlaylistsRepository = spyk()

    private val mockPlayList = mockk<List<Playlist>>()
    private val expected = Result.success(mockPlayList)
    private val exception = RuntimeException("Something went wrong")

    fun mockSuccessfulCase(): PlaylistViewModel {
        runBlocking {
            coEvery { mockRepository.getPlaylists() } returns flow {
                emit(expected)
            }
        }
        return PlaylistViewModel(mockRepository)
    }

    @Test
    fun getPlaylistsFromTheRepository() {
        viewModel = mockSuccessfulCase()
        runBlocking {
            viewModel.playlists.getValueForTest()

            coVerify(exactly = 1) { mockRepository.getPlaylists() }
        }

    }

    @Test
    fun emitsPlaylistsFromRepository() {
        viewModel = mockSuccessfulCase()
        runBlocking {
            assertEquals(expected, viewModel.playlists.getValueForTest())
        }
    }

    @Test
    fun emitErrorWhenReceiveError() {
        runBlocking {
            coEvery { mockRepository.getPlaylists() } returns flow {
                emit(Result.failure(exception))
            }

            viewModel = PlaylistViewModel(mockRepository)
            assertEquals(exception, viewModel.playlists.getValueForTest()!!.exceptionOrNull())
        }

    }
}