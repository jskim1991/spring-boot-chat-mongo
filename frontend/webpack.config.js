const path = require('path')

const HtmlWebpackPlugin = require('html-webpack-plugin')
const { WebpackManifestPlugin } = require('webpack-manifest-plugin')
const { CleanWebpackPlugin } = require('clean-webpack-plugin')

module.exports = {
    entry: './src/index.js',

    output: {
        publicPath: '',
        path: path.join(__dirname, 'dist'),
        filename: 'app.[contenthash].js',
    },

    devServer: {
        compress: false,
        port: 3000,
        proxy: {
            '/api/*': {
                target: 'http://localhost:8080',
                pathRewrite: { '^/api': '' },
            },
        },
    },

    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                include: path.join(__dirname, 'src'),
                loader: 'babel-loader',
                options: {
                    presets: [
                        [
                            '@babel/preset-env',
                            {
                                targets: {
                                    browsers: ['> 5% in KR', 'last 2 chrome versions'],
                                },
                                debug: true,
                            },
                        ],
                        ['@babel/preset-react', { runtime: 'automatic' }],
                    ],
                },
            },
            {
                test: /\.css$/i,
                use: ['style-loader', 'css-loader'],
            },
        ],
    },

    plugins: [
        new CleanWebpackPlugin(),
        new HtmlWebpackPlugin({
            inject: true,
            template: path.join(__dirname, 'public/index.html'),
        }),
        new WebpackManifestPlugin({
            fileName: 'manifest.json',
        }),
    ],
}
