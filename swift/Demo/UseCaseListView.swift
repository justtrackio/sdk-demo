import SwiftUI

struct UseCaseListView: View {
    @ObservedObject var viewModel = UseCaseListViewModel()

    var body: some View {
        NavigationView {
            List {
                ForEach(viewModel.useCases, id: \.name) { useCase in
                    HStack {
                        VStack(alignment: .leading) {
                            Text(useCase.name)
                                .font(.headline)
                            Text(useCase.description)
                                .font(.subheadline)
                        }

                        Spacer()

                        Button(action: useCase.onTry) {
                            Text("Try")
                                .font(.system(size: 14, weight: .bold))
                                .foregroundColor(.white)
                                .padding()
                                .background(.blue)
                                .cornerRadius(10)
                        }
                    }
                }
            }
            .listStyle(PlainListStyle())
            .buttonStyle(BorderlessButtonStyle())
            .navigationBarTitle("Use Cases")
        }
        .overlay(
            Group {
                if viewModel.isTrying {
                    Color.black.opacity(0.5).edgesIgnoringSafeArea(.all)
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        .scaleEffect(2)
                }
            }
            .animation(.easeInOut, value: viewModel.isTrying)
            .transition(.opacity)
        )
        .alert(isPresented: $viewModel.showAlert) {
            Alert(
                title: Text(viewModel.alertTitle),
                message: Text(viewModel.alertMessage),
                dismissButton: .default(Text("OK"))
            )
        }
    }
}
